package com.shannonsumner.namesofjesus.data

/** Inline formatting supported in devotional HTML. How each one looks is decided by the UI. */
enum class TextFormat { Bold, Italic, Underline, Superscript, Subscript }

/**
 * A run of text with the same formatting.
 *
 * [isScriptureLink] marks text from a `<button data-scripture>` element. Its
 * [scripture] is null when the attribute is missing or malformed; the text is
 * still shown as a link, but it does nothing when tapped (as on the website).
 */
data class RichTextRun(
    val text: String,
    val formats: Set<TextFormat> = emptySet(),
    val isScriptureLink: Boolean = false,
    val scripture: ScriptureSelection? = null
)

/** Styled text parsed from devotional HTML, independent of any UI toolkit. */
data class RichText(val runs: List<RichTextRun>) {
    val plainText: String get() = runs.joinToString(separator = "") { it.text }
}
