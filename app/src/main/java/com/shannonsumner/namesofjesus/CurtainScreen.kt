package com.shannonsumner.namesofjesus

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CurtainScreen() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val layout = curtainLayoutForWindow(maxWidth, maxHeight)
        CurtainBackground(layout)
        Image(
            painter = painterResource(R.drawable.curtain_background),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(layout.artworkWidth(maxWidth, maxHeight))
                .aspectRatio(535.32f / 800f)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(layout.titleAuthorGap),
            modifier = Modifier
                .align(
                    if (layout.centerContent) Alignment.Center else Alignment.TopCenter
                )
                .then(
                    if (layout.compactLandscape) {
                        Modifier
                            .fillMaxSize()
                            .safeDrawingPadding()
                            .verticalScroll(rememberScrollState())
                    } else {
                        Modifier
                    }
                )
                .padding(
                    top = when {
                        layout.compactLandscape -> 12.dp
                        layout.centerContent -> 0.dp
                        else -> WindowInsets.statusBars.asPaddingValues()
                            .calculateTopPadding() + layout.titleAuthorGap * 1.5f
                    },
                    bottom = if (layout.compactLandscape) 12.dp else 0.dp
                )
        ) {
            CurtainTitle(layout)
            Text(
                text = stringResource(R.string.curtain_author),
                color = Color.Black,
                fontFamily = Seshat,
                fontSize = layout.authorFontSize,
                fontWeight = FontWeight.Light,
                lineHeight = layout.authorFontSize * (34f / 28f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CurtainTitle(layout: CurtainLayout) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CursiveTitleLine(R.string.curtain_title_the, layout)
        TitleLine(R.string.curtain_title_wonderful, layout)
        TitleLine(R.string.curtain_title_names, layout)
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                8.dp * (layout.titleFontSize.value / 36f)
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CursiveTitleLine(R.string.curtain_title_of, layout)
            TitleLine(R.string.curtain_title_our, layout)
        }
        TitleLine(R.string.curtain_title_wonderful, layout)
        TitleLine(R.string.curtain_title_lord, layout)
    }
}

@Composable
private fun TitleLine(textRes: Int, layout: CurtainLayout) {
    Text(
        text = stringResource(textRes),
        color = Color.Black,
        fontFamily = Seshat,
        fontSize = layout.titleFontSize,
        fontWeight = FontWeight.Light,
        letterSpacing = layout.titleLetterSpacing,
        lineHeight = layout.titleFontSize * (41f / 36f)
    )
}

@Composable
private fun CursiveTitleLine(textRes: Int, layout: CurtainLayout) {
    Text(
        text = stringResource(textRes),
        color = Color.Black,
        fontFamily = Italianno,
        fontSize = layout.titleFontSize,
        letterSpacing = layout.titleLetterSpacing,
        lineHeight = layout.titleFontSize * (41f / 36f)
    )
}

@Composable
private fun CurtainBackground(layout: CurtainLayout) {
    val backgroundColor = colorResource(R.color.curtain_background)
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(backgroundColor)
        val stripePeriod = layout.stripeEnd.toPx()
        val stripeStart = layout.stripeStart.toPx()
        val stripeWidth = stripePeriod - stripeStart
        var x = 0f
        while (x < size.width) {
            drawRect(
                color = Color.Black.copy(alpha = 0.06f),
                topLeft = Offset(x + stripeStart, 0f),
                size = Size(stripeWidth, size.height)
            )
            x += stripePeriod
        }
    }
}

private val Seshat = FontFamily(Font(R.font.seshat, FontWeight.Normal))
private val Italianno = FontFamily(Font(R.font.italianno, FontWeight.Normal))

internal data class CurtainLayout(
    val titleFontSize: TextUnit,
    val authorFontSize: TextUnit,
    val titleLetterSpacing: TextUnit,
    val titleAuthorGap: Dp,
    val stripeStart: Dp,
    val stripeEnd: Dp,
    val centerContent: Boolean,
    val mobileArtwork: Boolean,
    val compactLandscape: Boolean = false
) {
    fun artworkWidth(windowWidth: Dp, windowHeight: Dp = Dp.Infinity): Dp {
        val width = if (mobileArtwork) windowWidth * 0.72f else {
            minOf(468.dp, windowWidth * 0.42f)
        }
        return if (compactLandscape) {
            minOf(width, windowHeight * 0.85f * (535.32f / 800f))
        } else {
            width
        }
    }
}

internal fun curtainLayoutForWindow(width: Dp, height: Dp): CurtainLayout {
    val layout = curtainLayoutForWidth(width)
    return if (width > height && height <= 600.dp) {
        layout.copy(
            titleFontSize = 36.sp,
            authorFontSize = 28.sp,
            titleLetterSpacing = 1.92.sp,
            titleAuthorGap = 12.dp,
            centerContent = false,
            compactLandscape = true
        )
    } else {
        layout
    }
}

internal fun curtainLayoutForWidth(width: Dp): CurtainLayout = when {
    width <= 600.dp -> CurtainLayout(
        titleFontSize = 36.sp,
        authorFontSize = 28.sp,
        titleLetterSpacing = 1.92.sp,
        titleAuthorGap = 24.dp,
        stripeStart = 14.dp,
        stripeEnd = 24.dp,
        centerContent = false,
        mobileArtwork = true
    )
    width <= 1024.dp -> CurtainLayout(
        titleFontSize = 48.sp,
        authorFontSize = 38.sp,
        titleLetterSpacing = 4.sp,
        titleAuthorGap = 32.dp,
        stripeStart = 20.dp,
        stripeEnd = 34.dp,
        centerContent = false,
        mobileArtwork = false
    )
    else -> CurtainLayout(
        titleFontSize = 60.sp,
        authorFontSize = 48.sp,
        titleLetterSpacing = 6.4.sp,
        titleAuthorGap = 48.dp,
        stripeStart = 30.dp,
        stripeEnd = 50.dp,
        centerContent = true,
        mobileArtwork = false
    )
}

@Preview(name = "Phone", widthDp = 390, heightDp = 844)
@Preview(name = "Tablet", widthDp = 800, heightDp = 1280)
@Preview(name = "Small tablet landscape", widthDp = 960, heightDp = 600)
@Preview(name = "Phone landscape", widthDp = 844, heightDp = 390)
@Preview(name = "Large window", widthDp = 1200, heightDp = 1000)
@Composable
private fun CurtainScreenPreview() {
    CurtainScreen()
}
