package com.shannonsumner.namesofjesus.data

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import org.json.JSONObject

@Database(
    entities = [BibleVerse::class],
    version = 1,
    exportSchema = false
)
abstract class KjvDatabase : RoomDatabase() {
    abstract fun bibleDao(): BibleDao

    companion object {
        /** The database file name in the app's private storage. */
        const val FILE_NAME = "kjv.sqlite"

        internal fun seedCallback(context: Context): Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                // Stream one verse per line to avoid loading the entire Bible into memory.
                // Room's creation transaction rolls back both imports if either fails.
                db.compileStatement(
                    "INSERT INTO verses (book, chapter, verse, text) VALUES (?, ?, ?, ?)"
                ).use { statement ->
                    context.assets.open("databases/kjv-verses.jsonl")
                        .bufferedReader(Charsets.UTF_8)
                        .useLines { lines ->
                            lines.forEach { line ->
                                val verse = JSONObject(line)
                                statement.bindLong(1, verse.getLong("book"))
                                statement.bindLong(2, verse.getLong("chapter"))
                                statement.bindLong(3, verse.getLong("verse"))
                                statement.bindString(4, verse.getString("text"))
                                statement.executeInsert()
                            }
                        }
                }
            }
        }
    }
}
