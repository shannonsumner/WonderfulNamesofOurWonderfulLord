package com.shannonsumner.namesofjesus.ui.share

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.shannonsumner.namesofjesus.ui.theme.Accent
import com.shannonsumner.namesofjesus.ui.theme.DefaultTextStyle
import com.shannonsumner.namesofjesus.ui.theme.Inter
import com.shannonsumner.namesofjesus.ui.theme.PopupBackground
import com.shannonsumner.namesofjesus.ui.theme.TextPrimary

// Matches the website's ShareButtons.css and CopyrightModal.css.
internal object ShareButtonsStyle {
    val ButtonSize = 44.dp
    val IconSize = 21.dp
    val ButtonGap = 8.dp
    val BorderWidth = 1.dp
    val BorderColor = Accent.copy(alpha = 0.45f)
    val ContainerColor = PopupBackground
    val ContentColor = Accent
    // Pressed or focused buttons invert, like the site's :hover and :focus-visible.
    val ActiveContainerColor = Accent
    val ActiveContentColor = PopupBackground

    /** Distance from the panel's bottom-right corner; smaller on phones. */
    fun edgeOffset(isCompact: Boolean): Dp = if (isCompact) 16.dp else 24.dp

    /** Extra space below the reading so its end can scroll clear of the buttons. */
    fun readingClearance(isCompact: Boolean): Dp = ButtonSize + edgeOffset(isCompact)
}

internal object CopyrightDialogStyle {
    val ContainerColor = PopupBackground
    val ParagraphGap = 12.dp

    val Title = DefaultTextStyle.copy(
        color = TextPrimary,
        fontFamily = Inter,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
    val Text = DefaultTextStyle.copy(
        color = TextPrimary,
        fontFamily = Inter,
        fontSize = 16.sp,
        lineHeight = 1.6.em
    )
}
