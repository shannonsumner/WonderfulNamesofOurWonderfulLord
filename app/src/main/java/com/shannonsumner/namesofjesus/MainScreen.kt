package com.shannonsumner.namesofjesus

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Timing and distance match the companion site's Curtain.css `raise-curtain` animation.
private const val CurtainRaiseDelayMillis = 1000
private const val CurtainRaiseDurationMillis = 3500
private const val CurtainRaiseDistance = 1.1f // 110% of the curtain's height

private val CurtainShadowHeight = 28.dp
private val CurtainShadowColor = Color.Black.copy(alpha = 0.28f)

/** Shows the daily reading panel, with the curtain on top until it finishes rising. */
@Composable
internal fun MainScreen() {
    var curtainRaised by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
        DailyReadingPanel()
        if (!curtainRaised) {
            RaisingCurtain(onRaised = { curtainRaised = true })
        }
    }
}

/** Waits briefly, raises the curtain off screen, then calls [onRaised]. */
@Composable
private fun RaisingCurtain(onRaised: () -> Unit) {
    // 0 = curtain fully down, 1 = curtain fully raised.
    val raiseProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        raiseProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = CurtainRaiseDurationMillis,
                delayMillis = CurtainRaiseDelayMillis,
                easing = LinearEasing
            )
        )
        onRaised()
    }

    CurtainScreen(
        modifier = Modifier
            .raisedBy { raiseProgress.value }
            .bottomEdgeShadow(CurtainShadowHeight, CurtainShadowColor)
    )
}

/** Moves content upward by [CurtainRaiseDistance] of its height as [progress] goes from 0 to 1. */
private fun Modifier.raisedBy(progress: () -> Float): Modifier = graphicsLayer {
    translationY = -size.height * CurtainRaiseDistance * progress()
}

/** Draws a soft shadow just below the content that fades from [color] to transparent. */
private fun Modifier.bottomEdgeShadow(height: Dp, color: Color): Modifier = drawBehind {
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
