package com.shannonsumner.namesofjesus.data

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.parser.Parser
import org.jsoup.safety.Cleaner
import org.jsoup.safety.Safelist
import org.jsoup.select.NodeTraversor
import org.jsoup.select.NodeVisitor

/**
 * Turns the devotional's stored HTML into [RichText], mirroring the website's
 * `sanitize-html` rules and how the browser then renders the result.
 *
 * [Jsoup](https://jsoup.org) parses the HTML, decodes entities, and removes
 * everything except `b`/`strong`, `i`/`em`, `u`, `sup`, `sub`, `br`, and
 * `button[data-scripture]`. Other tags are dropped but their text is kept,
 * except for tags such as `script` whose content is discarded too. This file
 * then converts the cleaned HTML into styled runs.
 */
object DevotionalHtml {
    /** Multi-paragraph fields are stored with a blank line between paragraphs. */
    private const val PARAGRAPH_SEPARATOR = "\n\n"

    private val SCRIPTURE_ATTRIBUTE = Regex("""^(\d+):(\d+):(\d+(?:-\d+)?)$""")
    private val COLLAPSIBLE_WHITESPACE = Regex("""[ \t\n\r\u000C]+""")
    private const val DISCARDED_CONTENT_TAGS = "script, style, textarea, option, noscript"
    private val FORMAT_TAGS = mapOf(
        "b" to TextFormat.Bold,
        "strong" to TextFormat.Bold,
        "i" to TextFormat.Italic,
        "em" to TextFormat.Italic,
        "u" to TextFormat.Underline,
        "sup" to TextFormat.Superscript,
        "sub" to TextFormat.Subscript
    )
    private val SAFELIST = Safelist()
        .addTags(*FORMAT_TAGS.keys.toTypedArray(), "br", "button")
        .addAttributes("button", "data-scripture")

    /**
     * Parses a multi-paragraph field such as `devo` or `activity`.
     *
     * Like the website, if any paragraph's text contains a single line break
     * (usually poetry), the whole field is kept as one block with its whitespace
     * intact. Otherwise each paragraph is returned separately with whitespace collapsed.
     */
    fun parseBody(html: String?): DevotionalBody {
        if (html.isNullOrBlank()) return DevotionalBody(emptyList(), preserveWhitespace = false)

        val rawParagraphs = html.split(PARAGRAPH_SEPARATOR)
        val preserveWhitespace = rawParagraphs.any { paragraph ->
            clean(paragraph).nodeStream(TextNode::class.java).anyMatch { '\n' in it.wholeText }
        }
        val paragraphs = if (preserveWhitespace) {
            listOf(parse(html, preserveWhitespace = true))
        } else {
            rawParagraphs.map { parse(it, preserveWhitespace = false) }
        }
        return DevotionalBody(
            paragraphs = paragraphs.filter { it.plainText.isNotBlank() },
            preserveWhitespace = preserveWhitespace
        )
    }

    /** Parses one block of HTML. Unclosed tags end with the block, as in a browser. */
    fun parse(html: String, preserveWhitespace: Boolean = false): RichText {
        val builder = RichTextBuilder(preserveWhitespace)
        NodeTraversor.traverse(builder, clean(html))
        return builder.build()
    }

    /** Decodes HTML character references such as `&hellip;` and `&#8217;`. */
    fun decodeEntities(text: String): String =
        if ('&' in text) Parser.unescapeEntities(text, false) else text

    /** Reads a `data-scripture` value like `43:3:16-18` into a passage, or null if invalid. */
    fun parseScripture(value: String?): ScriptureSelection? {
        val match = SCRIPTURE_ATTRIBUTE.matchEntire(value?.trim() ?: return null) ?: return null
        val book = match.groupValues[1].toIntOrNull() ?: return null
        val chapter = match.groupValues[2].toIntOrNull() ?: return null
        val verse = match.groupValues[3]
        val name = bookName(book) ?: return null
        return ScriptureSelection(book, chapter, verse, "$name $chapter:$verse")
    }

    /** Parses [html] and keeps only the allowed tags; returns the cleaned `<body>`. */
    private fun clean(html: String): Element {
        val document = Jsoup.parseBodyFragment(html)
        document.select(DISCARDED_CONTENT_TAGS).remove()
        return Cleaner(SAFELIST).clean(document).body()
    }

    /** Walks the cleaned HTML, tracking open formatting and links, and collects styled runs. */
    private class RichTextBuilder(private val preserveWhitespace: Boolean) : NodeVisitor {
        private val runs = ArrayList<MutableRun>()
        private val formats = ArrayList<TextFormat>()
        private var link: ScriptureLink? = null
        private var linkElement: Element? = null

        override fun head(node: Node, depth: Int) {
            when {
                node is TextNode -> appendText(node.wholeText)
                node !is Element -> Unit
                node.normalName() == "br" -> appendLineBreak()
                node.normalName() == "button" -> if (link == null) {
                    // A nested button cannot start a second link.
                    link = ScriptureLink(parseScripture(node.attr("data-scripture")))
                    linkElement = node
                }
                else -> FORMAT_TAGS[node.normalName()]?.let { formats += it }
            }
        }

        override fun tail(node: Node, depth: Int) {
            if (node !is Element) return
            if (node === linkElement) {
                link = null
                linkElement = null
            } else if (node.normalName() in FORMAT_TAGS) {
                formats.removeAt(formats.lastIndex)
            }
        }

        fun build(): RichText {
            if (!preserveWhitespace) trimTrailingSpaces()
            return RichText(
                runs.filter { it.text.isNotEmpty() }.map {
                    RichTextRun(it.text.toString(), it.formats, it.link != null, it.link?.scripture)
                }
            )
        }

        private fun appendText(rawText: String) {
            var text = if (preserveWhitespace) rawText else rawText.replace(COLLAPSIBLE_WHITESPACE, " ")
            if (!preserveWhitespace && text.startsWith(' ') && endsWithCollapsibleBoundary()) {
                text = text.substring(1)
            }
            append(text)
        }

        private fun appendLineBreak() {
            if (!preserveWhitespace) trimTrailingSpaces()
            append("\n")
        }

        private fun append(text: String) {
            if (text.isEmpty()) return
            val formats = formats.toCollection(LinkedHashSet())
            val last = runs.lastOrNull()
            if (last != null && last.formats == formats && last.link === link) {
                last.text.append(text)
            } else {
                runs += MutableRun(StringBuilder(text), formats, link)
            }
        }

        private fun endsWithCollapsibleBoundary(): Boolean {
            val lastChar = runs.lastOrNull { it.text.isNotEmpty() }?.text?.last() ?: return true
            return lastChar == ' ' || lastChar == '\n'
        }

        private fun trimTrailingSpaces() {
            while (runs.isNotEmpty()) {
                val text = runs.last().text
                while (text.isNotEmpty() && text.last() == ' ') text.setLength(text.length - 1)
                if (text.isNotEmpty()) return
                runs.removeAt(runs.lastIndex)
            }
        }
    }

    private class ScriptureLink(val scripture: ScriptureSelection?)

    private class MutableRun(
        val text: StringBuilder,
        val formats: Set<TextFormat>,
        val link: ScriptureLink?
    )
}
