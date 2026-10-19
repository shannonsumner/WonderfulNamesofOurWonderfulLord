package com.shannonsumner.namesofjesus.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KjvDatabaseTest {
    @Test
    fun roomImportsSourceDataAndPreservesItWhenReopened() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "kjv-test.sqlite"
        context.deleteDatabase(name)

        fun openDatabase() = Room.databaseBuilder(context, KjvDatabase::class.java, name)
            .addCallback(KjvDatabase.seedCallback(context))
            .build()

        try {
            val database = openDatabase()
            try {
                val dao = database.bibleDao()
                assertEquals(31102, dao.getVerseCount())
                val actualMetadata = dao.getMetadata().associate { it.field to it.value }
                val metadata = context.assets.open("databases/kjv-metadata.json")
                    .bufferedReader(Charsets.UTF_8)
                    .use { JSONArray(it.readText()) }
                assertEquals(metadata.length(), actualMetadata.size)
                for (index in 0 until metadata.length()) {
                    val entry = metadata.getJSONObject(index)
                    val value = if (entry.isNull("value")) null else entry.getString("value")
                    assertEquals(value, actualMetadata[entry.getString("field")])
                }

                var chapterKey: Pair<Int, Int>? = null
                var chapterVerses = emptyList<BibleVerse>()
                context.assets.open("databases/kjv-verses.jsonl")
                    .bufferedReader(Charsets.UTF_8)
                    .useLines { lines ->
                        lines.forEach { line ->
                            val row = JSONObject(line)
                            val expected = BibleVerse(
                                row.getInt("id"),
                                row.getInt("book"),
                                row.getInt("chapter"),
                                row.getInt("verse"),
                                row.getString("text")
                            )
                            val key = expected.book to expected.chapter
                            if (key != chapterKey) {
                                chapterVerses = dao.getChapter(expected.book, expected.chapter)
                                chapterKey = key
                            }
                            assertEquals(expected, chapterVerses.single { it.verse == expected.verse })
                        }
                    }
                assertEquals(
                    dao.getChapter(1, 1).filter { it.verse in 1..3 },
                    dao.getPassage(1, 1, 1, 3)
                )
                assertEquals(1, dao.getVerse(1, 1, 1)?.id)
                assertEquals(31102, dao.getVerse(66, 22, 21)?.id)
                assertNull(dao.getVerse(1, 1, 0))
            } finally {
                database.close()
            }
            val reopened = openDatabase()
            try {
                assertEquals(31102, reopened.bibleDao().getVerseCount())
                assertEquals(31102, reopened.bibleDao().getVerse(66, 22, 21)?.id)
            } finally {
                reopened.close()
            }
        } finally {
            context.deleteDatabase(name)
        }
    }
}
