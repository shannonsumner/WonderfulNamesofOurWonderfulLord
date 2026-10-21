package com.shannonsumner.namesofjesus.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DevotionalHtmlTest {
    @Test
    fun scriptureButtonsBecomeLinksWithReferences() {
        val text = DevotionalHtml.parse(
            "High (<button data-scripture=\"50:2:9\">Philippians 2:9</button>; " +
                "<button data-scripture='43:3:16-18'>John <em>3</em></button>)"
        )

        assertEquals("High (Philippians 2:9; John 3)", text.plainText)
        assertEquals(
            listOf(
                RichTextRun("High ("),
                RichTextRun(
                    "Philippians 2:9",
                    isScriptureLink = true,
                    scripture = ScriptureSelection(50, 2, "9", "Philippians 2:9")
                ),
                RichTextRun("; "),
                RichTextRun(
                    "John ",
                    isScriptureLink = true,
                    scripture = ScriptureSelection(43, 3, "16-18", "John 3:16-18")
                ),
                RichTextRun(
                    "3",
                    formats = setOf(TextFormat.Italic),
                    isScriptureLink = true,
                    scripture = ScriptureSelection(43, 3, "16-18", "John 3:16-18")
                ),
                RichTextRun(")")
            ),
            text.runs
        )
    }

    @Test
    fun invalidScriptureButtonsStayLinksButCannotOpen() {
        for (value in listOf("", "67:1:1", "0:1:1", "John 3:16", "43:3")) {
            val run = DevotionalHtml.parse("<button data-scripture=\"$value\">x</button>").runs.single()
            assertTrue(run.isScriptureLink)
            assertNull(value, run.scripture)
        }
        assertNull(DevotionalHtml.parse("<button>x</button>").runs.single().scripture)
    }

    @Test
    fun decodesEntities() {
        assertEquals(
            "And… “a & b” — it’s <x> &unknown;",
            DevotionalHtml.decodeEntities(
                "And&hellip; &ldquo;a &amp; b&rdquo; &#x2014; it&#8217;s &lt;x&gt; &unknown;"
            )
        )
        assertEquals("And… a", DevotionalHtml.parse("And&hellip; a").plainText)
    }

    @Test
    fun collapsesWhitespaceLikeNormalHtmlParagraphs() {
        val text = DevotionalHtml.parse("  Hello \t  <em> world </em>  ")

        assertEquals(
            listOf(RichTextRun("Hello "), RichTextRun("world", setOf(TextFormat.Italic))),
            text.runs
        )
    }

    @Test
    fun keepsSupportedFormattingAndLineBreaks() {
        val text = DevotionalHtml.parse(
            "<b>B</b><strong>S</strong><i>I</i><u>U</u><sup>1</sup><sub>2</sub> a <br/> b"
        )

        assertEquals("BSIU12 a\nb", text.plainText)
        assertEquals(
            listOf(
                setOf(TextFormat.Bold),
                setOf(TextFormat.Italic),
                setOf(TextFormat.Underline),
                setOf(TextFormat.Superscript),
                setOf(TextFormat.Subscript),
                emptySet()
            ),
            text.runs.map { it.formats }
        )
    }

    @Test
    fun removesUnsupportedTagsAndDangerousContent() {
        val text = DevotionalHtml.parse(
            "<p class=\"x\">Keep <a href=\"https://example.com\">this</a></p>" +
                "<script>alert('no')</script><!-- note --><STYLE>p{}</STYLE> <EM>text"
        )

        assertEquals("Keep this text", text.plainText)
        assertEquals(setOf(TextFormat.Italic), text.runs.last().formats)
    }

    @Test
    fun splitsBodyIntoParagraphs() {
        val body = DevotionalHtml.parseBody("First  paragraph.\n\nSecond <em>one</em>.\n\n   ")

        assertFalse(body.preserveWhitespace)
        assertEquals(listOf("First paragraph.", "Second one."), body.paragraphs.map { it.plainText })
    }

    @Test
    fun keepsPoetryWhitespaceAsOneBlock() {
        val html = "Until\n\n<em>All through the mountains,\n     And up from the rocky steep,</em>"
        val body = DevotionalHtml.parseBody(html)

        assertTrue(body.preserveWhitespace)
        assertEquals(
            listOf("Until\n\nAll through the mountains,\n     And up from the rocky steep,"),
            body.paragraphs.map { it.plainText }
        )
    }

    @Test
    fun missingBodyIsEmpty() {
        assertTrue(DevotionalHtml.parseBody(null).isEmpty)
        assertTrue(DevotionalHtml.parseBody("  ").isEmpty)
    }

    @Test
    fun mapsDatabaseRowsForDisplay() {
        val row = DailyDevotional(
            dayOfYear = 60,
            topic = "A Branch",
            scripture = "And&hellip; a Branch",
            reference = "Isaiah 11:1",
            book = 23,
            chapter = 11,
            verse = "1",
            devo = "Text",
            activity = null
        )

        val devotional = row.toDevotional()
        assertEquals("And… a Branch", devotional.scripture)
        assertEquals(ScriptureSelection(23, 11, "1", "Isaiah 11:1"), devotional.mainScripture)
        assertTrue(devotional.activity.isEmpty)
        assertNull(row.copy(book = null).toDevotional().mainScripture)
        assertNull(row.copy(verse = "").toDevotional().mainScripture)
    }
}
