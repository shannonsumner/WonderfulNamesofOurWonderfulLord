package com.shannonsumner.namesofjesus.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class DevotionalRepositoryTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var database: DevotionalDatabase
    private lateinit var repository: DevotionalRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(context, DevotionalDatabase::class.java)
            .addCallback(DevotionalDatabase.seedCallback(context))
            .build()
        repository = RoomDevotionalRepository(database.dailyDevotionalDao())
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun leapDayHasItsOwnReadingAndLaterDatesMatchInEveryYear() = runBlocking {
        val dao = database.dailyDevotionalDao()
        val leapDay = repository.getDevotional(LocalDate.of(2024, 2, 29))
        assertEquals(dao.getByDayOfYear(60)!!.topic, leapDay?.topic)

        val marchFirst = dao.getByDayOfYear(61)!!.topic
        assertEquals(marchFirst, repository.getDevotional(LocalDate.of(2024, 3, 1))?.topic)
        assertEquals(marchFirst, repository.getDevotional(LocalDate.of(2025, 3, 1))?.topic)
        assertEquals(
            dao.getByDayOfYear(366)!!.topic,
            repository.getDevotional(LocalDate.of(2025, 12, 31))?.topic
        )
    }

    @Test
    fun everyDevotionalParsesWithoutLeftoverMarkup() = runBlocking {
        var scriptureLinks = 0
        for (dayOfYear in 1..366) {
            val devotional = requireNotNull(repository.getDevotional(LocalDate.ofYearDay(2024, dayOfYear)))
            assertNotNull("Day $dayOfYear main scripture", devotional.mainScripture)
            val texts = listOf(devotional.topic, devotional.scripture) +
                (devotional.devo.paragraphs + devotional.activity.paragraphs).map { it.plainText }
            for (text in texts) {
                assertFalse("Day $dayOfYear has markup: $text", Regex("<[a-zA-Z/]|&[a-zA-Z#]\\w*;").containsMatchIn(text))
            }
            for (paragraph in devotional.devo.paragraphs + devotional.activity.paragraphs) {
                paragraph.runs.forEachIndexed { index, run ->
                    if (!run.isScriptureLink) return@forEachIndexed
                    assertNotNull("Day $dayOfYear has a broken scripture link", run.scripture)
                    val previous = paragraph.runs.getOrNull(index - 1)
                    if (previous?.isScriptureLink != true || previous.scripture != run.scripture) scriptureLinks++
                }
            }
        }
        assertEquals(58, scriptureLinks)
    }
}
