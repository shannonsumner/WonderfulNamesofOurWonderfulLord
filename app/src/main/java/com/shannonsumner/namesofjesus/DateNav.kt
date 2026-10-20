package com.shannonsumner.namesofjesus

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val DateNavFont = FontFamily(Font(R.font.inter, FontWeight.Normal))

/** Date controls and a month/day picker, styled to match the companion web app. */
@Composable
internal fun DateNav(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    isCompact: Boolean,
    modifier: Modifier = Modifier,
    today: LocalDate = LocalDate.now()
) {
    var isCalendarOpen by rememberSaveable { mutableStateOf(false) }
    var viewMonthValue by rememberSaveable { mutableIntStateOf(date.monthValue) }
    var viewYear by rememberSaveable { mutableIntStateOf(date.year) }
    val viewMonth = remember(viewYear, viewMonthValue) {
        YearMonth.of(viewYear, viewMonthValue)
    }
    val monthName = date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val previousDayLabel = stringResource(R.string.date_nav_previous_day)
    val nextDayLabel = stringResource(R.string.date_nav_next_day)
    val chooseDateLabel = stringResource(R.string.date_nav_choose_date)
    val accentColor = colorResource(R.color.date_nav_accent)
    val dividerColor = colorResource(R.color.date_nav_divider)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (isCompact) 24.dp else 48.dp,
                    vertical = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DateNavAction(
                label = previousDayLabel,
                arrow = "←",
                isCompact = isCompact,
                accentColor = accentColor,
                onClick = { onDateChange(date.minusDays(1)) }
            )
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(
                            role = Role.Button,
                            onClick = {
                                viewMonthValue = date.monthValue
                                viewYear = date.year
                                isCalendarOpen = !isCalendarOpen
                            }
                        )
                        .semantics {
                            contentDescription = chooseDateLabel
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$monthName ${date.dayOfMonth}",
                        color = Color.Black,
                        fontFamily = DateNavFont,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Canvas(modifier = Modifier.size(width = 12.dp, height = 8.dp)) {
                        val stroke = 2.dp.toPx()
                        drawLine(
                            color = Color.Black,
                            start = androidx.compose.ui.geometry.Offset(1.dp.toPx(), 1.dp.toPx()),
                            end = androidx.compose.ui.geometry.Offset(size.width / 2, size.height - 1.dp.toPx()),
                            strokeWidth = stroke,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.Black,
                            start = androidx.compose.ui.geometry.Offset(size.width / 2, size.height - 1.dp.toPx()),
                            end = androidx.compose.ui.geometry.Offset(size.width - 1.dp.toPx(), 1.dp.toPx()),
                            strokeWidth = stroke,
                            cap = StrokeCap.Round
                        )
                    }
                }
                if (isCalendarOpen) {
                    CalendarPopup(
                        date = date,
                        today = today,
                        viewMonth = viewMonth,
                        isCompact = isCompact,
                        onPreviousMonth = {
                            val previous = viewMonth.minusMonths(1)
                            viewMonthValue = previous.monthValue
                            viewYear = previous.year
                        },
                        onNextMonth = {
                            val next = viewMonth.plusMonths(1)
                            viewMonthValue = next.monthValue
                            viewYear = next.year
                        },
                        onSelectDay = { day ->
                            onDateChange(
                                LocalDate.of(date.year, viewMonth.monthValue, 1)
                                    .plusDays((day - 1).toLong())
                            )
                            isCalendarOpen = false
                        },
                        onDismiss = { isCalendarOpen = false }
                    )
                }
            }
            DateNavAction(
                label = nextDayLabel,
                arrow = "→",
                isCompact = isCompact,
                accentColor = accentColor,
                onClick = { onDateChange(date.plusDays(1)) }
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 16.dp)
                .background(dividerColor)
        )
    }
}

@Composable
private fun DateNavAction(
    label: String,
    arrow: String,
    isCompact: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = label }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (label == stringResource(R.string.date_nav_previous_day)) {
            Text(
                text = arrow,
                color = accentColor,
                fontFamily = DateNavFont,
                fontSize = 16.sp
            )
            if (!isCompact) DateNavActionLabel(label)
        } else {
            if (!isCompact) DateNavActionLabel(label)
            Text(
                text = arrow,
                color = accentColor,
                fontFamily = DateNavFont,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun DateNavActionLabel(label: String) {
    Text(
        text = label,
        color = colorResource(R.color.date_nav_accent),
        fontFamily = DateNavFont,
        fontSize = 16.sp
    )
}

@Composable
private fun CalendarPopup(
    date: LocalDate,
    today: LocalDate,
    viewMonth: YearMonth,
    isCompact: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDay: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val accentColor = colorResource(R.color.date_nav_accent)
    val selectedDayColor = colorResource(R.color.date_nav_selected_day)
    val weekdayLabels = stringArrayResource(R.array.date_nav_weekday_labels)
    val chooseMonthAndDayLabel = stringResource(R.string.date_nav_choose_month_and_day)
    val popupOffset = with(density) {
        IntOffset(
            x = if (isCompact) 0 else (-20).dp.roundToPx(),
            y = 48.dp.roundToPx()
        )
    }
    val firstWeekday = viewMonth.atDay(1).dayOfWeek.value % 7
    val daysInMonth = viewMonth.lengthOfMonth()
    val totalCells = ((firstWeekday + daysInMonth + 6) / 7) * 7
    val viewedMonthName = viewMonth.atDay(1).month
        .getDisplayName(TextStyle.FULL, Locale.getDefault())
    val selectedMonth = date.monthValue == viewMonth.monthValue &&
        date.year == viewMonth.year
    val todayMonth = today.monthValue == viewMonth.monthValue &&
        today.year == viewMonth.year

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
            color = Color.White,
            shape = RoundedCornerShape(6.dp),
            shadowElevation = 12.dp,
            modifier = Modifier
                .width(240.dp)
                .semantics {
                    contentDescription = chooseMonthAndDayLabel
                }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MonthControl(
                        "‹",
                        stringResource(R.string.date_nav_previous_month),
                        accentColor,
                        onPreviousMonth
                    )
                    Text(
                        text = viewedMonthName,
                        color = Color.Black,
                        fontFamily = DateNavFont,
                        fontSize = 16.sp
                    )
                    MonthControl(
                        "›",
                        stringResource(R.string.date_nav_next_month),
                        accentColor,
                        onNextMonth
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    weekdayLabels.forEach { label ->
                        Text(
                            text = label,
                            color = Color.Black.copy(alpha = 0.6f),
                            fontFamily = DateNavFont,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(totalCells / 7) { week ->
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(7) { weekday ->
                                val cell = week * 7 + weekday
                                val day = cell - firstWeekday + 1
                                if (day in 1..daysInMonth) {
                                    CalendarDay(
                                        day = day,
                                        monthName = viewedMonthName,
                                        isSelected = selectedMonth && date.dayOfMonth == day,
                                        isToday = todayMonth && today.dayOfMonth == day,
                                        accentColor = accentColor,
                                        selectedDayColor = selectedDayColor,
                                        onClick = { onSelectDay(day) }
                                    )
                                } else {
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                    )
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
private fun MonthControl(
    label: String,
    description: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = accentColor,
            fontFamily = DateNavFont,
            fontSize = 24.sp
        )
    }
}

@Composable
private fun RowScope.CalendarDay(
    day: Int,
    monthName: String,
    isSelected: Boolean,
    isToday: Boolean,
    accentColor: Color,
    selectedDayColor: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(4.dp)
    val dayDescription = stringResource(R.string.date_nav_day_description, monthName, day)
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(shape)
            .background(if (isSelected) selectedDayColor else Color.Transparent)
            .then(
                if (isToday) Modifier.border(1.dp, accentColor, shape)
                else Modifier
            )
            .clickable(role = Role.Button, onClick = onClick)
            .semantics {
                contentDescription = dayDescription
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = Color.Black,
            fontFamily = DateNavFont,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
