package com.shannonsumner.namesofjesus.ui.main

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
import androidx.compose.ui.graphics.graphicsLayer
import com.shannonsumner.namesofjesus.ui.curtain.CurtainScreen
import com.shannonsumner.namesofjesus.ui.dailyreading.DailyReadingPanel

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
