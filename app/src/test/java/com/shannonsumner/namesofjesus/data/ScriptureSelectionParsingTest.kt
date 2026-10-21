package com.shannonsumner.namesofjesus.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScriptureSelectionParsingTest {
    @Test
    fun parsesSingleVersesRangesAndLists() {
        assertEquals(listOf(16), parseVerseSelection("16"))
        assertEquals(listOf(16, 17, 18), parseVerseSelection("16-18"))
        assertEquals(listOf(4, 5, 9), parseVerseSelection("4-5,9"))
        assertEquals(listOf(4, 5, 9), parseVerseSelection(" 4 - 5 , 9 "))
    }

    @Test
    fun keepsSelectionOrderWithoutDuplicates() {
        assertEquals(listOf(9, 4, 5), parseVerseSelection("9,4-5,5,9"))
    }

    @Test
    fun acceptsALeadingChapterOnlyWhenItMatches() {
        assertEquals(listOf(1, 2), parseVerseSelection("4:1-2", chapter = 4))
        assertNull(parseVerseSelection("4:1-2", chapter = 3))
        assertNull(parseVerseSelection("4:1-2"))
    }

    @Test
    fun rejectsMalformedOrOutOfRangeSelections() {
        listOf("", " ", "0", "5-3", "177", "1-177", "a", "1,", "1--2", "x".repeat(513))
            .forEach { assertNull("'$it'", parseVerseSelection(it)) }
        assertEquals(176, parseVerseSelection("1-176")?.size)
    }

    @Test
    fun displayTextDropsLeadingPilcrowOnly() {
        assertEquals("And it came to pass ¶ here", displayVerseText("¶ And it came to pass ¶ here "))
        assertEquals("‹Verily› [is]", displayVerseText("‹Verily› [is]"))
    }
}
