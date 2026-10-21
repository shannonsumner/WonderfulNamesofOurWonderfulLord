package com.shannonsumner.namesofjesus.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.Year

class DevotionalDayOfYearTest {
    @Test
    fun februaryTwentyNinthIsAlwaysDaySixty() {
        assertEquals(1, devotionalDayOfYear(1, 1))
        assertEquals(59, devotionalDayOfYear(2, 28))
        assertEquals(60, devotionalDayOfYear(2, 29))
        assertEquals(61, devotionalDayOfYear(3, 1))
        assertEquals(366, devotionalDayOfYear(12, 31))
    }

    @Test
    fun datesAfterFebruaryMapToTheSameDayInLeapAndCommonYears() {
        for (year in listOf(2000, 2023, 2024, 2025, 2100)) {
            assertEquals(59, devotionalDayOfYear(LocalDate.of(year, 2, 28)))
            assertEquals(61, devotionalDayOfYear(LocalDate.of(year, 3, 1)))
            assertEquals(366, devotionalDayOfYear(LocalDate.of(year, 12, 31)))
        }
        assertEquals(60, devotionalDayOfYear(LocalDate.of(2000, 2, 29)))
        assertEquals(60, devotionalDayOfYear(LocalDate.of(2024, 2, 29)))
    }

    @Test
    fun leapYearsUseEveryDayAndCommonYearsSkipOnlyFebruaryTwentyNinth() {
        fun daysIn(year: Int) = (1..Year.of(year).length()).map {
            devotionalDayOfYear(LocalDate.ofYearDay(year, it))
        }

        assertEquals((1..366).toList(), daysIn(2024))
        assertEquals((1..366).filter { it != 60 }, daysIn(2025))
        assertEquals((1..366).filter { it != 60 }, daysIn(2100))
    }
}
