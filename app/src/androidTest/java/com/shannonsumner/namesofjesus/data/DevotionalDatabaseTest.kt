package com.shannonsumner.namesofjesus.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DevotionalDatabaseTest {
    @Test
    fun roomCreatesEveryDayAndPreservesDataWhenReopened() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "devotional-test.sqlite"
        context.deleteDatabase(name)

        fun openDatabase() = Room.databaseBuilder(
            context,
            DevotionalDatabase::class.java,
            name
        )
            .addCallback(DevotionalDatabase.seedCallback(context))
            .build()

        try {
            val database = openDatabase()
            val devotionals = try {
                val dao = database.dailyDevotionalDao()
                val all = dao.getAll()
                assertEquals((1..366).toList(), all.map { it.dayOfYear })
                for (devotional in all) {
                    assertEquals(devotional, dao.getByDayOfYear(devotional.dayOfYear))
                }
                assertNull(dao.getByDayOfYear(0))
                assertNull(dao.getByDayOfYear(367))
                all
            } finally {
                database.close()
            }
            val reopened = openDatabase()
            try {
                assertEquals(devotionals, reopened.dailyDevotionalDao().getAll())
            } finally {
                reopened.close()
            }
        } finally {
            context.deleteDatabase(name)
        }
    }
}
