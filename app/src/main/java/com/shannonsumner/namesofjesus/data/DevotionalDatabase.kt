package com.shannonsumner.namesofjesus.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import org.json.JSONArray

@Database(entities = [DailyDevotional::class], version = 1, exportSchema = true)
abstract class DevotionalDatabase : RoomDatabase() {
    abstract fun dailyDevotionalDao(): DailyDevotionalDao

    companion object {
        @Volatile
        private var instance: DevotionalDatabase? = null

        fun getInstance(context: Context): DevotionalDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DevotionalDatabase::class.java,
                    "daily-devotional.sqlite"
                )
                    .addCallback(seedCallback(context.applicationContext))
                    .build()
                    .also { instance = it }
            }

        internal fun seedCallback(context: Context): Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                val devotionals = context.assets.open("databases/daily-devotionals.json")
                    .bufferedReader(Charsets.UTF_8)
                    .use { JSONArray(it.readText()) }

                // Room runs onCreate inside the creation transaction, so a failed import rolls back.
                for (index in 0 until devotionals.length()) {
                    val devotional = devotionals.getJSONObject(index)
                    db.execSQL(
                        """
                        INSERT INTO Daily
                            (day_of_year, topic, scripture, reference, book, chapter, verse, devo, activity)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """.trimIndent(),
                        arrayOf<Any?>(
                            devotional.getInt("day_of_year"),
                            devotional.getString("topic"),
                            devotional.getString("scripture"),
                            devotional.getString("reference"),
                            if (devotional.isNull("book")) null else devotional.getInt("book"),
                            if (devotional.isNull("chapter")) null else devotional.getInt("chapter"),
                            devotional.getString("verse"),
                            if (devotional.isNull("devo")) null else devotional.getString("devo"),
                            if (devotional.isNull("activity")) null else devotional.getString("activity")
                        )
                    )
                }
            }
        }
    }
}
