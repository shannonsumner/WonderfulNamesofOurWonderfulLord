package com.shannonsumner.namesofjesus.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.MonthDay
import javax.inject.Inject

/**
 * The devotional data has 366 rows and no year. Every date is looked up as if
 * it fell in this leap year, so day 60 is always February 29 and March 1 is
 * always day 61, whether or not the real year is a leap year. In common years
 * the February 29 reading is simply never reached by date.
 */
private const val ReferenceLeapYear = 2024

/** Maps a calendar [month] (1–12) and [day] of that month to the devotional's `day_of_year` (1–366). */
fun devotionalDayOfYear(month: Int, day: Int): Int =
    MonthDay.of(month, day).atYear(ReferenceLeapYear).dayOfYear

/** Maps any [date], in any year, to the devotional's `day_of_year` (1–366). */
fun devotionalDayOfYear(date: LocalDate): Int = devotionalDayOfYear(date.monthValue, date.dayOfMonth)

/** Loads devotionals by calendar date, ready for display. */
interface DevotionalRepository {
    /** The devotional for [date]'s month and day, or null if there is none. */
    suspend fun getDevotional(date: LocalDate): Devotional?
}

/** Reads devotionals from Room and converts their HTML for display. */
class RoomDevotionalRepository @Inject constructor(
    private val dao: DailyDevotionalDao
) : DevotionalRepository {
    override suspend fun getDevotional(date: LocalDate): Devotional? =
        dao.getByDayOfYear(devotionalDayOfYear(date))?.let { row ->
            withContext(Dispatchers.Default) { row.toDevotional() }
        }
}

internal fun DailyDevotional.toDevotional(): Devotional = Devotional(
    topic = DevotionalHtml.decodeEntities(topic),
    scripture = DevotionalHtml.decodeEntities(scripture),
    reference = reference,
    mainScripture = if (book != null && book != 0 && chapter != null && chapter != 0 && verse.isNotEmpty()) {
        ScriptureSelection(book, chapter, verse, reference)
    } else {
        null
    },
    devo = DevotionalHtml.parseBody(devo),
    activity = DevotionalHtml.parseBody(activity)
)
