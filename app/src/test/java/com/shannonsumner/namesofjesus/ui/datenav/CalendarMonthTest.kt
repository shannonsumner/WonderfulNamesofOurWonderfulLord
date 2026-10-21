package com.shannonsumner.namesofjesus.ui.datenav

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

class CalendarMonthTest {
    @Test
    fun weeksStartOnSundayAndPadToFullRows() {
        // February 2024 starts on a Thursday and has 29 days.
        val calendar = calendarMonth(
            YearMonth.of(2024, 2),
            selected = LocalDate.of(2024, 2, 10),
            today = LocalDate.of(2024, 2, 29)
        )
        assertEquals(5, calendar.weeks.size)
        assertTrue(calendar.weeks.all { it.size == 7 })
        assertEquals(listOf(null, null, null, null), calendar.weeks[0].take(4))
        assertEquals(1, calendar.weeks[0][4]?.day)
        assertEquals(29, calendar.weeks[4][4]?.day)
        assertNull(calendar.weeks[4][5])
        assertEquals(29, calendar.weeks.flatten().count { it != null })
    }

    @Test
    fun marksSelectedAndTodayOnlyInTheirOwnMonthAndYear() {
        val selected = LocalDate.of(2024, 3, 5)
        val today = LocalDate.of(2024, 3, 9)
        val days = calendarMonth(YearMonth.of(2024, 3), selected, today).weeks.flatten().filterNotNull()
        assertEquals(listOf(5), days.filter { it.isSelected }.map { it.day })
        assertEquals(listOf(9), days.filter { it.isToday }.map { it.day })

        val otherYear = calendarMonth(YearMonth.of(2025, 3), selected, today).weeks.flatten().filterNotNull()
        assertFalse(otherYear.any { it.isSelected || it.isToday })
    }

    @Test
    fun selectedDateKeepsCurrentYearAndRollsImpossibleDaysForward() {
        val current = LocalDate.of(2025, 1, 31)
        assertEquals(LocalDate.of(2025, 2, 14), selectedDateFor(current, YearMonth.of(2024, 2), 14))
        // February 29 doesn't exist in 2025, so it becomes March 1, as on the website.
        assertEquals(LocalDate.of(2025, 3, 1), selectedDateFor(current, YearMonth.of(2024, 2), 29))
        assertEquals(
            LocalDate.of(2024, 2, 29),
            selectedDateFor(LocalDate.of(2024, 1, 1), YearMonth.of(2024, 2), 29)
        )
    }

    @Test
    fun dateLabelOmitsYear() {
        assertEquals("February 29", dateLabel(LocalDate.of(2024, 2, 29), Locale.US))
    }
}
