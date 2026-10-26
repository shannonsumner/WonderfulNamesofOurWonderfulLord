package com.shannonsumner.namesofjesus.ui.datenav

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.shannonsumner.namesofjesus.R
import com.shannonsumner.namesofjesus.ui.theme.WonderfulNamesTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

private enum class DateNavDirection { Previous, Next }

/** Date controls with a Material-styled calendar and a year-free reading label. */
@Composable
internal fun DateNav(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    val pickerState = rememberDatePickerState(date)
    if (pickerState.isOpen) {
        ReadingDatePicker(
            date = date,
            pickerState = pickerState,
            onDateChange = onDateChange
        )
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (isCompact) {
                        DateNavStyle.PhoneHorizontalPadding
                    } else {
                        DateNavStyle.HorizontalPadding
                    },
                    vertical = DateNavStyle.VerticalPadding
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DateNavAction(
                direction = DateNavDirection.Previous,
                showLabel = !isCompact,
                onClick = { onDateChange(date.minusDays(1)) }
            )
            DateButton(date = date, onClick = { pickerState.toggle(date) })
            DateNavAction(
                direction = DateNavDirection.Next,
                showLabel = !isCompact,
                onClick = { onDateChange(date.plusDays(1)) }
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(DateNavStyle.DividerThickness)
                .padding(horizontal = DateNavStyle.DividerInset)
                .background(DateNavStyle.DividerColor)
        )
    }
}

@Composable
private fun DateButton(date: LocalDate, onClick: () -> Unit) {
    val chooseDateLabel = stringResource(R.string.date_nav_choose_date)
    Row(
        modifier = Modifier
            .clip(DateNavStyle.ControlShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = chooseDateLabel }
            .padding(DateNavStyle.ControlPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DateNavStyle.ControlSpacing)
    ) {
        Text(text = dateLabel(date), style = DateNavStyle.DateLabel)
        Chevron()
    }
}

/** The small downward chevron beside the date. */
@Composable
private fun Chevron() {
    Canvas(
        modifier = Modifier.size(
            width = DateNavStyle.ChevronWidth,
            height = DateNavStyle.ChevronHeight
        )
    ) {
        val stroke = DateNavStyle.ChevronStroke.toPx()
        val inset = 1.dp.toPx()
        val point = Offset(size.width / 2, size.height - inset)
        drawLine(
            color = DateNavStyle.ChevronColor,
            start = Offset(inset, inset),
            end = point,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = DateNavStyle.ChevronColor,
            start = point,
            end = Offset(size.width - inset, inset),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun DateNavAction(direction: DateNavDirection, showLabel: Boolean, onClick: () -> Unit) {
    val label = stringResource(
        when (direction) {
            DateNavDirection.Previous -> R.string.date_nav_previous_day
            DateNavDirection.Next -> R.string.date_nav_next_day
        }
    )
    Row(
        modifier = Modifier
            .clip(DateNavStyle.ControlShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = label }
            .padding(DateNavStyle.ControlPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DateNavStyle.ControlSpacing)
    ) {
        when (direction) {
            DateNavDirection.Previous -> {
                Text(text = "←", style = DateNavStyle.Action)
                if (showLabel) Text(text = label, style = DateNavStyle.Action)
            }
            DateNavDirection.Next -> {
                if (showLabel) Text(text = label, style = DateNavStyle.Action)
                Text(text = "→", style = DateNavStyle.Action)
            }
        }
    }
}

@Composable
private fun ReadingDatePicker(
    date: LocalDate,
    pickerState: DatePickerState,
    onDateChange: (LocalDate) -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]
    Dialog(onDismissRequest = pickerState::close) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 360.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column {
                Column(
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 20.dp,
                        bottom = 20.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = pickerState.viewMonth.month.getDisplayName(TextStyle.FULL, locale),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MonthControl(
                                symbol = "‹",
                                description = stringResource(R.string.date_nav_previous_month),
                                onClick = pickerState::showPreviousMonth
                            )
                            MonthControl(
                                symbol = "›",
                                description = stringResource(R.string.date_nav_next_month),
                                onClick = pickerState::showNextMonth
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val firstDay = WeekFields.of(locale).firstDayOfWeek
                        (0 until 7).forEach { offset ->
                            val day = firstDay.plus(offset.toLong())
                            Text(
                                text = day.getDisplayName(TextStyle.NARROW, locale),
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        val calendar = calendarMonth(
                            pickerState.viewMonth,
                            date,
                            LocalDate.now(),
                            WeekFields.of(locale).firstDayOfWeek
                        )
                        calendar.weeks.forEach { week ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                week.forEach { day ->
                                    if (day == null) {
                                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                                    } else {
                                        CalendarDayCell(day, pickerState.viewMonth, locale) {
                                            onDateChange(pickerState.select(date, day.day))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthControl(symbol: String, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(DateNavStyle.ControlShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description }
            .size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 30.sp,
            lineHeight = 32.sp
        )
    }
}

@Composable
private fun RowScope.CalendarDayCell(
    day: CalendarDay,
    month: YearMonth,
    locale: Locale,
    onClick: () -> Unit
) {
    val shape = MaterialTheme.shapes.extraLarge
    val monthName = month.month.getDisplayName(TextStyle.FULL, locale)
    val dayDescription = stringResource(R.string.date_nav_day_description, monthName, day.day)
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(shape)
            .then(
                if (day.isSelected) Modifier.background(MaterialTheme.colorScheme.primary)
                else Modifier
            )
            .then(
                if (day.isToday && !day.isSelected) {
                    Modifier.border(1.dp, MaterialTheme.colorScheme.primary, shape)
                } else {
                    Modifier
                }
            )
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = dayDescription },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.day.toString(),
            color = if (day.isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        )
    }
}

@Preview(name = "Phone", widthDp = 390)
@Preview(name = "Tablet", widthDp = 800)
@Composable
private fun DateNavPreview() {
    WonderfulNamesTheme {
        DateNav(
            date = LocalDate.of(2024, 2, 29),
            onDateChange = {},
            isCompact = false
        )
    }
}
