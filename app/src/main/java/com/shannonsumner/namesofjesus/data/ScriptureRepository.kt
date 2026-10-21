package com.shannonsumner.namesofjesus.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** One KJV verse ready to display. */
data class ScriptureVerse(val verse: Int, val text: String)

private const val MaxVerse = 176 // Psalm 119
private const val MaxSelectionLength = 512
private val VersePart = Regex("""^(\d+)(?:\s*-\s*(\d+))?$""")

/**
 * Parses a verse selection such as `16`, `16-18`, or `4-5,9` into verse numbers,
 * in order and without duplicates, like the website's `parseVerseSelection`.
 * Returns null if the selection is empty or malformed.
 *
 * Unlike the website, a leading chapter that matches [chapter] is accepted, so
 * the source's `4:1-2` for 2 Timothy 4 opens verses 1–2.
 */
fun parseVerseSelection(selection: String, chapter: Int? = null): List<Int>? {
    if (selection.isEmpty() || selection.length > MaxSelectionLength) return null
    val verses = selection.trim()
        .let { if (chapter != null) it.removePrefix("$chapter:") else it }
    val result = LinkedHashSet<Int>()
    for (part in verses.split(',')) {
        val match = VersePart.matchEntire(part.trim()) ?: return null
        val start = match.groupValues[1].toIntOrNull() ?: return null
        val end = match.groupValues[2].ifEmpty { match.groupValues[1] }.toIntOrNull() ?: return null
        if (start < 1 || end < start || end > MaxVerse) return null
        result.addAll(start..end)
    }
    return result.toList().ifEmpty { null }
}

/** KJV text as shown in the passage popup: the leading pilcrow is removed, as on the website. */
internal fun displayVerseText(text: String): String = text.replace(Regex("""^¶\s*"""), "").trim()

/** Looks up passages tapped in the devotional. */
interface ScriptureRepository {
    /** Opens the Bible, building it from the bundled source on first run. */
    suspend fun prepare()

    /**
     * Returns the selected verses in selection order, or null if the selection
     * is invalid or any verse is missing (the website responds 400/404 then).
     */
    suspend fun getPassage(selection: ScriptureSelection): List<ScriptureVerse>?
}

/** Reads passages from the KJV Room database. */
class RoomScriptureRepository @Inject constructor(private val dao: BibleDao) : ScriptureRepository {
    override suspend fun prepare() {
        dao.getVerseCount()
    }

    override suspend fun getPassage(selection: ScriptureSelection): List<ScriptureVerse>? {
        if (selection.book !in 1..BookNames.size || selection.chapter < 1) return null
        val verses = parseVerseSelection(selection.verse, selection.chapter) ?: return null
        val rows = dao.getVerses(selection.book, selection.chapter, verses)
        return withContext(Dispatchers.Default) {
            val textByVerse = rows.associate { it.verse to it.text }
            if (verses.any { it !in textByVerse }) {
                null
            } else {
                verses.map { ScriptureVerse(it, displayVerseText(textByVerse.getValue(it))) }
            }
        }
    }
}
