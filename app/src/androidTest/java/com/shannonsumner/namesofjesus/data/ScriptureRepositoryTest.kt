package com.shannonsumner.namesofjesus.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class ScriptureRepositoryTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var bible: KjvDatabase
    private lateinit var scriptures: ScriptureRepository

    @Before
    fun setUp() {
        bible = Room.inMemoryDatabaseBuilder(context, KjvDatabase::class.java)
            .addCallback(KjvDatabase.seedCallback(context))
            .build()
        scriptures = RoomScriptureRepository(bible.bibleDao())
    }

    @After
    fun tearDown() = bible.close()

    @Test
    fun returnsVersesInSelectionOrder() = runBlocking {
        val passage = scriptures.getPassage(ScriptureSelection(43, 3, "17,16", "John 3:17,16"))
        assertEquals(listOf(17, 16), passage?.map { it.verse })
        assertTrue(passage!![1].text.startsWith("‹For God so loved the world"))
    }

    @Test
    fun missingVersesOrBooksAreUnavailable() = runBlocking {
        assertNull(scriptures.getPassage(ScriptureSelection(43, 3, "36-37", "John 3:36-37")))
        assertNull(scriptures.getPassage(ScriptureSelection(67, 1, "1", "?")))
        assertNull(scriptures.getPassage(ScriptureSelection(43, 3, "x", "?")))
    }

    @Test
    fun everyDevotionalScriptureOpens() = runBlocking {
        val database = Room.inMemoryDatabaseBuilder(context, DevotionalDatabase::class.java)
            .addCallback(DevotionalDatabase.seedCallback(context))
            .build()
        try {
            val devotionals = RoomDevotionalRepository(database.dailyDevotionalDao())
            for (dayOfYear in 1..366) {
                val devotional = requireNotNull(devotionals.getDevotional(LocalDate.ofYearDay(2024, dayOfYear)))
                val selections = listOfNotNull(devotional.mainScripture) +
                    (devotional.devo.paragraphs + devotional.activity.paragraphs)
                        .flatMap { paragraph -> paragraph.runs.mapNotNull { it.scripture } }
                for (selection in selections.distinct()) {
                    val passage = scriptures.getPassage(selection)
                    assertNotNull("Day $dayOfYear: ${selection.reference} (${selection.verse})", passage)
                    assertTrue(passage!!.none { it.text.isEmpty() || it.text.startsWith("¶") })
                }
            }
        } finally {
            database.close()
        }
    }
}
