package com.shannonsumner.namesofjesus.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import org.json.JSONArray
import org.json.JSONObject

@Database(
    entities = [BibleVerse::class, BibleMetadata::class],
    version = 1,
    exportSchema = true
)
abstract class KjvDatabase : RoomDatabase() {
    abstract fun bibleDao(): BibleDao

    companion object {
        @Volatile
        private var instance: KjvDatabase? = null

        fun getInstance(context: Context): KjvDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    KjvDatabase::class.java,
                    "kjv.sqlite"
                )
                    .addCallback(seedCallback(context.applicationContext))
                    .build()
                    .also { instance = it }
            }

        internal fun seedCallback(context: Context): Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                val metadata = context.assets.open("databases/kjv-metadata.json")
                    .bufferedReader(Charsets.UTF_8)
                    .use { JSONArray(it.readText()) }
                db.compileStatement("INSERT INTO meta (field, value) VALUES (?, ?)").use { statement ->
                    for (index in 0 until metadata.length()) {
                        val entry = metadata.getJSONObject(index)
                        statement.bindString(1, entry.getString("field"))
                        if (entry.isNull("value")) {
                            statement.bindNull(2)
                        } else {
                            statement.bindString(2, entry.getString("value"))
                        }
                        statement.executeInsert()
                    }
                }

                // Stream one verse per line to avoid loading the entire Bible into memory.
                // Room's creation transaction rolls back both imports if either fails.
                db.compileStatement(
                    "INSERT INTO verses (id, book, chapter, verse, text) VALUES (?, ?, ?, ?, ?)"
                ).use { statement ->
                    context.assets.open("databases/kjv-verses.jsonl")
                        .bufferedReader(Charsets.UTF_8)
                        .useLines { lines ->
                            lines.forEach { line ->
                                val verse = JSONObject(line)
                                statement.bindLong(1, verse.getLong("id"))
                                statement.bindLong(2, verse.getLong("book"))
                                statement.bindLong(3, verse.getLong("chapter"))
                                statement.bindLong(4, verse.getLong("verse"))
                                statement.bindString(5, verse.getString("text"))
                                statement.executeInsert()
                            }
                        }
                }
            }
        }
    }
}
