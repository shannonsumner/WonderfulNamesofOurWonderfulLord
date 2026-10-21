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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.shannonsumner.namesofjesus.R
import com.shannonsumner.namesofjesus.ui.theme.WonderfulNamesTheme
import java.time.LocalDate

private enum class DateNavDirection { Previous, Next }

/** Date controls and a month/day picker, styled to match the companion web app. */
@Composable
internal fun DateNav(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    isCompact: Boolean,
    modifier: Modifier = Modifier,
    today: LocalDate = LocalDate.now(),
    pickerState: DatePickerState = rememberDatePickerState(date)
) {
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
            Box {
                DateButton(date = date, onClick = { pickerState.toggle(date) })
                if (pickerState.isOpen) {
                    CalendarPopup(
                        calendar = calendarMonth(pickerState.viewMonth, date, today),
                        isCompact = isCompact,
                        onPreviousMonth = pickerState::showPreviousMonth,
                        onNextMonth = pickerState::showNextMonth,
                        onSelectDay = { day -> onDateChange(pickerState.select(date, day)) },
                        onDismiss = pickerState::close
                    )
                }
            }
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
private fun CalendarPopup(
    calendar: CalendarMonth,
    isCompact: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDay: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val popupOffset = with(LocalDensity.current) {
        IntOffset(
            x = if (isCompact) 0 else DateNavStyle.PopupOffsetX.roundToPx(),
            y = DateNavStyle.PopupOffsetY.roundToPx()
        )
    }
    val chooseMonthAndDayLabel = stringResource(R.string.date_nav_choose_month_and_day)
    val viewedMonthName = monthName(calendar.month)

    Popup(
        alignment = if (isCompact) Alignment.TopCenter else Alignment.TopEnd,
        offset = popupOffset,
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            color = DateNavStyle.PopupColor,
            shape = DateNavStyle.PopupShape,
            shadowElevation = DateNavStyle.PopupElevation,
            modifier = Modifier
                .width(DateNavStyle.PopupWidth)
                .semantics { contentDescription = chooseMonthAndDayLabel }
        ) {
            Column(modifier = Modifier.padding(DateNavStyle.PopupPadding)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = DateNavStyle.MonthHeaderBottomPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MonthControl("‹", stringResource(R.string.date_nav_previous_month), onPreviousMonth)
                    Text(text = viewedMonthName, style = DateNavStyle.MonthName)
                    MonthControl("›", stringResource(R.string.date_nav_next_month), onNextMonth)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(DateNavStyle.GridSpacing)) {
                    stringArrayResource(R.array.date_nav_weekday_labels).forEach { label ->
                        Text(
                            text = label,
                            style = DateNavStyle.Weekday,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(DateNavStyle.GridSpacing)) {
                    calendar.weeks.forEach { week ->
                        Row(horizontalArrangement = Arrangement.spacedBy(DateNavStyle.GridSpacing)) {
                            week.forEach { day ->
                                if (day != null) {
                                    CalendarDayCell(
                                        day = day,
                                        monthName = viewedMonthName,
                                        onClick = { onSelectDay(day.day) }
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
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
            .padding(
                horizontal = DateNavStyle.MonthControlHorizontalPadding,
                vertical = DateNavStyle.MonthControlVerticalPadding
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = symbol, style = DateNavStyle.MonthControl)
    }
}

@Composable
private fun RowScope.CalendarDayCell(day: CalendarDay, monthName: String, onClick: () -> Unit) {
    val shape = DateNavStyle.DayShape
    val dayDescription = stringResource(R.string.date_nav_day_description, monthName, day.day)
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(shape)
            .then(
                if (day.isSelected) Modifier.background(DateNavStyle.SelectedDayColor)
                else Modifier
            )
            .then(
                if (day.isToday) {
                    Modifier.border(DateNavStyle.TodayBorderWidth, DateNavStyle.TodayBorderColor, shape)
                } else {
                    Modifier
                }
            )
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = dayDescription },
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.day.toString(), style = DateNavStyle.Day)
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
            isCompact = false,
            today = LocalDate.of(2024, 2, 29)
        )
    }
}
