package com.shannonsumner.namesofjesus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import java.time.LocalDate

// Values match the companion site's DailyReading.css.
private val PanelMaxWidth = 960.dp
private val PanelCornerRadius = 6.dp
private val PanelShadowElevation = 16.dp
// Android lowers shadow opacity on its own, so full black ends up close to the site's 22%.
private val PanelShadowColor = Color.Black

/** Space between the screen edge and the panel; smaller on phones and tablets. */
private fun outerPaddingForWidth(width: Dp): Dp = if (width <= 1024.dp) 24.dp else 40.dp

/** The striped page background with an empty, translucent reading panel on top. */
@Composable
fun DailyReadingPanel(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCompact = maxWidth <= 600.dp
        StripedBackground(curtainLayoutForWidth(maxWidth))
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing) // keep clear of status/navigation bars
                .padding(outerPaddingForWidth(maxWidth))
        ) {
            ReadingPanel(isCompact = isCompact)
        }
    }
}

/** The rounded, translucent card that will hold the day's reading. */
@Composable
private fun ReadingPanel(isCompact: Boolean) {
    var today by remember { mutableStateOf(LocalDate.now()) }
    var selectedDateEpochDay by rememberSaveable {
        mutableLongStateOf(today.toEpochDay())
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        fun refreshDate() {
            today = LocalDate.now()
            if (selectedDateEpochDay != today.toEpochDay()) {
                selectedDateEpochDay = today.toEpochDay()
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshDate()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val selectedDate = remember(selectedDateEpochDay) {
        LocalDate.ofEpochDay(selectedDateEpochDay)
    }
    val shape = RoundedCornerShape(PanelCornerRadius)
    Box(
        modifier = Modifier
            .widthIn(max = PanelMaxWidth)
            .fillMaxSize()
    ) {
        PanelShadow(shape)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(colorResource(R.color.daily_reading_panel), shape)
        )
        Column(modifier = Modifier.fillMaxSize()) {
            DateNav(
                date = selectedDate,
                onDateChange = { selectedDateEpochDay = it.toEpochDay() },
                isCompact = isCompact,
                today = today
            )
        }
    }
}

/**
 * Draws a soft shadow around the panel, like CSS `box-shadow`.
 *
 * Android draws elevation shadows underneath the whole shape, so they would show
 * through the translucent panel. Clipping out the panel's shape keeps the shadow
 * visible only around its edges.
 */
@Composable
private fun BoxScope.PanelShadow(shape: Shape) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .drawWithContent {
                val panelOutline = Path().apply {
                    addOutline(shape.createOutline(size, layoutDirection, this@drawWithContent))
                }
                clipPath(panelOutline, clipOp = ClipOp.Difference) {
                    this@drawWithContent.drawContent()
                }
            }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .shadow(
                    elevation = PanelShadowElevation,
                    shape = shape,
                    ambientColor = PanelShadowColor,
                    spotColor = PanelShadowColor
                )
                .background(Color.White, shape)
        )
    }
}

@Preview(name = "Phone", widthDp = 390, heightDp = 844)
@Preview(name = "Tablet", widthDp = 800, heightDp = 1280)
@Preview(name = "Large window", widthDp = 1200, heightDp = 1000)
@Composable
private fun DailyReadingPanelPreview() {
    DailyReadingPanel()
}
