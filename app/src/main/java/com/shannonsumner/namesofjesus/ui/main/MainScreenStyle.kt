package com.shannonsumner.namesofjesus.ui.main

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shannonsumner.namesofjesus.ui.theme.CurtainShadow

// Timing and distance match the companion site's Curtain.css `raise-curtain` animation.
internal const val CurtainRaiseDelayMillis = 1000
internal const val CurtainRaiseDurationMillis = 3500
internal const val CurtainRaiseDistance = 1.1f // 110% of the curtain's height

internal val CurtainShadowHeight = 28.dp
internal val CurtainShadowColor = CurtainShadow

/** Draws a soft shadow just below the content that fades from [color] to transparent. */
internal fun Modifier.bottomEdgeShadow(height: Dp, color: Color): Modifier = drawBehind {
    val shadowHeight = height.toPx()
    val fadingShadow = Brush.verticalGradient(
        0f to color,
        0.35f to color.copy(alpha = color.alpha * 0.43f),
        0.7f to color.copy(alpha = color.alpha * 0.11f),
        1f to Color.Transparent,
        startY = size.height,
        endY = size.height + shadowHeight
    )
    drawRect(
        brush = fadingShadow,
        topLeft = Offset(0f, size.height),
        size = Size(size.width, shadowHeight)
    )
}
