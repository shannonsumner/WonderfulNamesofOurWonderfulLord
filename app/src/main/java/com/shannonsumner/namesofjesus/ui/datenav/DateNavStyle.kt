package com.shannonsumner.namesofjesus.ui.datenav

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shannonsumner.namesofjesus.ui.theme.Accent
import com.shannonsumner.namesofjesus.ui.theme.Divider
import com.shannonsumner.namesofjesus.ui.theme.DefaultTextStyle
import com.shannonsumner.namesofjesus.ui.theme.Inter
import com.shannonsumner.namesofjesus.ui.theme.PopupBackground
import com.shannonsumner.namesofjesus.ui.theme.SelectedDayBackground
import com.shannonsumner.namesofjesus.ui.theme.TextPrimary
import com.shannonsumner.namesofjesus.ui.theme.TextSecondary

/** Colors, sizes, and text styles for [DateNav], from the website's DateNav.css. */
internal object DateNavStyle {
    val PhoneHorizontalPadding = 24.dp
    val HorizontalPadding = 48.dp
    val VerticalPadding = 16.dp
    val ControlPadding = 8.dp
    val ControlSpacing = 8.dp
    val ControlShape = RoundedCornerShape(4.dp)

    val DividerColor = Divider
    val DividerThickness = 1.dp
    val DividerInset = 16.dp

    val ChevronColor = TextPrimary
    val ChevronWidth = 12.dp
    val ChevronHeight = 8.dp
    val ChevronStroke = 2.dp

    val DateLabel = DefaultTextStyle.copy(
        color = TextPrimary,
        fontFamily = Inter,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    val Action = DefaultTextStyle.copy(color = Accent, fontFamily = Inter, fontSize = 16.sp)

    // Calendar popup
    val PopupColor = PopupBackground
    val PopupShape = RoundedCornerShape(6.dp)
    val PopupElevation = 12.dp
    val PopupWidth = 240.dp
    val PopupPadding = 16.dp
    val PopupOffsetY = 48.dp
    val PopupOffsetX = (-20).dp
    val MonthHeaderBottomPadding = 12.dp
    val MonthControlHorizontalPadding = 8.dp
    val MonthControlVerticalPadding = 4.dp
    val GridSpacing = 4.dp
    val DayShape = RoundedCornerShape(4.dp)
    val SelectedDayColor = SelectedDayBackground
    val TodayBorderColor = Accent
    val TodayBorderWidth = 1.dp

    val MonthName = DefaultTextStyle.copy(color = TextPrimary, fontFamily = Inter, fontSize = 16.sp)
    val MonthControl = DefaultTextStyle.copy(color = Accent, fontFamily = Inter, fontSize = 24.sp)
    val Weekday = DefaultTextStyle.copy(
        color = TextSecondary,
        fontFamily = Inter,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    val Day = DefaultTextStyle.copy(
        color = TextPrimary,
        fontFamily = Inter,
        fontSize = 14.sp,
        textAlign = TextAlign.Center
    )
}
