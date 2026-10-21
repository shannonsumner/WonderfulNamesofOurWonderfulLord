package com.shannonsumner.namesofjesus.ui.dailyreading

import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shannonsumner.namesofjesus.data.ScriptureSelection
import com.shannonsumner.namesofjesus.ui.components.StripedBackground
import com.shannonsumner.namesofjesus.ui.datenav.DateNav
import com.shannonsumner.namesofjesus.ui.scripture.ScriptureDialog
import com.shannonsumner.namesofjesus.ui.theme.WindowWidth
import com.shannonsumner.namesofjesus.ui.theme.WonderfulNamesTheme
import java.time.LocalDate

/** The striped page background with the translucent reading panel on top. */
@Composable
fun DailyReadingPanel(
    modifier: Modifier = Modifier,
    viewModel: DailyReadingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Coming back to the app shows today's reading, like reloading the website.
    LifecycleResumeEffect(viewModel) {
        viewModel.returnToToday()
        onPauseOrDispose {}
    }
    DailyReadingPanel(
        uiState = uiState,
        onDateChange = viewModel::selectDate,
        onScriptureClick = viewModel::openScripture,
        onScriptureDismiss = viewModel::closeScripture,
        modifier = modifier
    )
}

/** Stateless version of the panel, driven entirely by [uiState]. */
@Composable
internal fun DailyReadingPanel(
    uiState: DailyReadingUiState,
    onDateChange: (LocalDate) -> Unit,
    onScriptureClick: (ScriptureSelection) -> Unit,
    onScriptureDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val windowWidth = WindowWidth.of(maxWidth)
        val isCompact = windowWidth == WindowWidth.Phone
        StripedBackground(windowWidth)
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing) // keep clear of status/navigation bars
                .padding(panelOuterPadding(windowWidth))
        ) {
            ReadingPanel(uiState, isCompact, onDateChange, onScriptureClick)
        }
        uiState.scripture?.let { ScriptureDialog(it, onDismiss = onScriptureDismiss) }
    }
}

/** The rounded, translucent card that holds the date controls and the day's reading. */
@Composable
private fun ReadingPanel(
    uiState: DailyReadingUiState,
    isCompact: Boolean,
    onDateChange: (LocalDate) -> Unit,
    onScriptureClick: (ScriptureSelection) -> Unit
) {
    val shape = RoundedCornerShape(PanelCornerRadius)
    val devotional = uiState.devotional
    // Start each newly loaded devotional at the top.
    val scrollState = rememberSaveable(devotional, saver = ScrollState.Saver) { ScrollState(0) }
    Box(
        modifier = Modifier
            .widthIn(max = PanelMaxWidth)
            .fillMaxSize()
    ) {
        PanelShadow(shape)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(PanelColor, shape)
        )
        Column(modifier = Modifier.fillMaxSize().clip(shape)) {
            DateNav(
                date = uiState.date,
                onDateChange = onDateChange,
                isCompact = isCompact,
                today = uiState.today
            )
            if (devotional != null) {
                DailyReading(
                    devotional = devotional,
                    isCompact = isCompact,
                    onScriptureClick = onScriptureClick,
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                )
            }
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
                .background(Color.White, shape) // opaque so the shadow casts; clipped out above
        )
    }
}

@Preview(name = "Phone", widthDp = 390, heightDp = 844)
@Preview(name = "Tablet", widthDp = 800, heightDp = 1280)
@Preview(name = "Large window", widthDp = 1200, heightDp = 1000)
@Composable
private fun DailyReadingPanelPreview() {
    val today = LocalDate.now()
    WonderfulNamesTheme {
        DailyReadingPanel(
            uiState = DailyReadingUiState(today = today, date = today, devotional = PreviewDevotional),
            onDateChange = {},
            onScriptureClick = {},
            onScriptureDismiss = {}
        )
    }
}
