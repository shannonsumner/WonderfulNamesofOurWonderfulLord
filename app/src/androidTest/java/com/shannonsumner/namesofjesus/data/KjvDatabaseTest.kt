package com.shannonsumner.namesofjesus.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
                val actualMetadata = database.query("SELECT field, value FROM meta", null).use { cursor ->
                    buildMap {
                        while (cursor.moveToNext()) {
                            put(cursor.getString(0), if (cursor.isNull(1)) null else cursor.getString(1))
                        }
                    }
                }
                val metadata = context.assets.open("databases/kjv-metadata.json")
                    .bufferedReader(Charsets.UTF_8)
                    .use { JSONArray(it.readText()) }
                assertEquals(metadata.length(), actualMetadata.size)
                for (index in 0 until metadata.length()) {
                    val entry = metadata.getJSONObject(index)
                    val value = if (entry.isNull("value")) null else entry.getString("value")
                    assertEquals(value, actualMetadata[entry.getString("field")])
                }

                val expectedByChapter = context.assets.open("databases/kjv-verses.jsonl")
                    .bufferedReader(Charsets.UTF_8)
                    .useLines { lines ->
                        lines.map { line ->
                            val row = JSONObject(line)
                            BibleVerse(
                                row.getInt("id"),
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
                assertEquals(listOf(1), dao.getVerses(1, 1, listOf(1)).map { it.id })
                assertEquals(listOf(31102), dao.getVerses(66, 22, listOf(21)).map { it.id })
                assertTrue(dao.getVerses(1, 1, listOf(0)).isEmpty())
            } finally {
                database.close()
            }
            val reopened = openDatabase()
            try {
                assertEquals(31102, reopened.bibleDao().getVerseCount())
                assertEquals(31102, reopened.bibleDao().getVerses(66, 22, listOf(21)).single().id)
            } finally {
                reopened.close()
            }
        } finally {
            context.deleteDatabase(name)
        }
    }
}
