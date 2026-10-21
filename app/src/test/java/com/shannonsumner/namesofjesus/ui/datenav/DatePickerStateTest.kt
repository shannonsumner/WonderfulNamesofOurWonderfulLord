package com.shannonsumner.namesofjesus.ui.datenav

import androidx.compose.runtime.saveable.SaverScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class DatePickerStateTest {
    @Test
    fun openingShowsTheCurrentDatesMonth() {
        val state = DatePickerState(isOpen = false, viewMonth = YearMonth.of(2024, 1))
        state.toggle(LocalDate.of(2024, 6, 15))
        assertTrue(state.isOpen)
        assertEquals(YearMonth.of(2024, 6), state.viewMonth)
        state.toggle(LocalDate.of(2024, 6, 15))
        assertFalse(state.isOpen)
    }

    @Test
    fun monthNavigationCrossesYearBoundaries() {
        val state = DatePickerState(isOpen = true, viewMonth = YearMonth.of(2024, 1))
        state.showPreviousMonth()
        assertEquals(YearMonth.of(2023, 12), state.viewMonth)
        state.showNextMonth()
        state.showNextMonth()
        assertEquals(YearMonth.of(2024, 2), state.viewMonth)
    }

    @Test
    fun selectingClosesAndReturnsDateInViewedMonth() {
        val state = DatePickerState(isOpen = true, viewMonth = YearMonth.of(2024, 2))
        assertEquals(LocalDate.of(2024, 2, 29), state.select(LocalDate.of(2024, 1, 31), 29))
        assertFalse(state.isOpen)
    }

    @Test
    fun saverRestoresState() {
        val state = DatePickerState(isOpen = true, viewMonth = YearMonth.of(2023, 11))
        val saved = with(DatePickerState.Saver) { SaverScope { true }.save(state) }!!
        val restored = DatePickerState.Saver.restore(saved)!!
        assertTrue(restored.isOpen)
        assertEquals(YearMonth.of(2023, 11), restored.viewMonth)
    }
}
