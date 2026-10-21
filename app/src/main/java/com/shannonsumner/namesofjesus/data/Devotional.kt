package com.shannonsumner.namesofjesus.data

/** A passage that can be opened from the devotional, such as `John 3:16-18`. */
data class ScriptureSelection(
    val book: Int,
    val chapter: Int,
    val verse: String,
    val reference: String
)

/** A day's devotional, ready to display. All text has been decoded and sanitized. */
data class Devotional(
    val topic: String,
    val scripture: String,
    val reference: String,
    /** The day's main passage, or null when the source row has no book, chapter, or verse. */
    val mainScripture: ScriptureSelection?,
    val devo: DevotionalBody,
    val activity: DevotionalBody
)

/**
 * A multi-paragraph devotional field.
 *
 * When [preserveWhitespace] is true, the source contains poetry or other single
 * line breaks, so [paragraphs] holds one block whose spaces and line breaks
 * must be kept exactly (like CSS `white-space: pre-wrap`). Otherwise each
 * paragraph's whitespace has already been collapsed.
 */
data class DevotionalBody(
    val paragraphs: List<RichText>,
    val preserveWhitespace: Boolean
) {
    val isEmpty: Boolean get() = paragraphs.isEmpty()
}
