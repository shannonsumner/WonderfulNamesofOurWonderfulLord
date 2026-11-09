package com.shannonsumner.namesofjesus.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BibleDatabaseTest {
    @Test
    fun roomImportsSourceDataAndPreservesItWhenReopened() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "kjv-test.sqlite"
        context.deleteDatabase(name)

        fun openDatabase() = Room.databaseBuilder(context, BibleDatabase::class.java, name)
            .addCallback(BibleDatabase.seedCallback(context))
            .build()

        try {
            val database = openDatabase()
            try {
                val dao = database.bibleDao()
                assertEquals(31102, dao.getVerseCount())
                val expectedByChapter = context.assets.open("databases/bible.json")
                    .bufferedReader(Charsets.UTF_8)
                    .useLines { lines ->
                        lines.map { line ->
                            val row = JSONObject(line)
                            assertEquals(4, row.length())
                            BibleVerse(
                                row.getInt("book"),
                                row.getInt("chapter"),
                                row.getInt("verse"),
                                row.getString("text")
                            )
                        }.groupBy { it.book to it.chapter }
                    }
                expectedByChapter.forEach { (key, expected) ->
                    val (book, chapter) = key
                    val actual = dao.getVerses(book, chapter, expected.map { it.verse })
                    assertEquals(expected.sortedBy { it.verse }, actual.sortedBy { it.verse })
                }
                assertEquals(
                    listOf(BibleVerse(1, 1, 1, "¶ In the beginning God created the heaven and the earth.")),
                    dao.getVerses(1, 1, listOf(1))
                )
                assertEquals(
                    listOf(BibleVerse(66, 22, 21, "The grace of our Lord Jesus Christ [be] with you all. Amen.")),
                    dao.getVerses(66, 22, listOf(21))
                )
                assertTrue(dao.getVerses(1, 1, listOf(0)).isEmpty())
            } finally {
                database.close()
            }
            val reopened = openDatabase()
            try {
                assertEquals(31102, reopened.bibleDao().getVerseCount())
                assertEquals(
                    BibleVerse(66, 22, 21, "The grace of our Lord Jesus Christ [be] with you all. Amen."),
                    reopened.bibleDao().getVerses(66, 22, listOf(21)).single()
                )
            } finally {
                reopened.close()
            }
        } finally {
            context.deleteDatabase(name)
        }
    }
}
