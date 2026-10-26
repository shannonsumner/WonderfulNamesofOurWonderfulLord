package com.shannonsumner.namesofjesus.ui.datenav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

/** One day shown in the calendar grid. */
internal data class CalendarDay(val day: Int, val isSelected: Boolean, val isToday: Boolean)

/** A month laid out as padded weeks of seven cells. */
internal data class CalendarMonth(val month: YearMonth, val weeks: List<List<CalendarDay?>>)

internal fun calendarMonth(
    month: YearMonth,
    selected: LocalDate,
    today: LocalDate,
    firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY
): CalendarMonth {
    val firstWeekday = (month.atDay(1).dayOfWeek.value % 7 - firstDayOfWeek.value % 7 + 7) % 7
    val daysInMonth = month.lengthOfMonth()
    val totalCells = ((firstWeekday + daysInMonth + 6) / 7) * 7
    val cells = (0 until totalCells).map { cell ->
        val day = cell - firstWeekday + 1
        if (day in 1..daysInMonth) {
            val date = month.atDay(day)
            CalendarDay(day = day, isSelected = date == selected, isToday = date == today)
        } else {
            null
        }
    }
    return CalendarMonth(month, cells.chunked(7))
}

/** The year is kept from [current]; impossible days roll forward in that year. */
internal fun selectedDateFor(current: LocalDate, viewMonth: YearMonth, day: Int): LocalDate =
    LocalDate.of(current.year, viewMonth.monthValue, 1).plusDays((day - 1).toLong())

@Stable
internal class DatePickerState(isOpen: Boolean, viewMonth: YearMonth) {
    var isOpen by mutableStateOf(isOpen)
        private set
    var viewMonth by mutableStateOf(viewMonth)
        private set

    fun toggle(date: LocalDate) {
        if (!isOpen) viewMonth = YearMonth.from(date)
        isOpen = !isOpen
    }

    fun close() {
        isOpen = false
    }

    fun showPreviousMonth() {
        viewMonth = viewMonth.minusMonths(1)
    }

    fun showNextMonth() {
        viewMonth = viewMonth.plusMonths(1)
    }

    fun select(current: LocalDate, day: Int): LocalDate {
        isOpen = false
        return selectedDateFor(current, viewMonth, day)
    }

    companion object {
        val Saver: Saver<DatePickerState, List<Int>> = Saver(
            save = { listOf(if (it.isOpen) 1 else 0, it.viewMonth.year, it.viewMonth.monthValue) },
            restore = { DatePickerState(it[0] == 1, YearMonth.of(it[1], it[2])) }
        )
    }
}

@Composable
internal fun rememberDatePickerState(date: LocalDate): DatePickerState =
    rememberSaveable(saver = DatePickerState.Saver) {
        DatePickerState(isOpen = false, viewMonth = YearMonth.from(date))
    }
