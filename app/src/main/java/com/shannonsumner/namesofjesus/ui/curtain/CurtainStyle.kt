package com.shannonsumner.namesofjesus.ui.curtain

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shannonsumner.namesofjesus.ui.theme.DefaultTextStyle
import com.shannonsumner.namesofjesus.ui.theme.Italianno
import com.shannonsumner.namesofjesus.ui.theme.Seshat
import com.shannonsumner.namesofjesus.ui.theme.TextPrimary
import com.shannonsumner.namesofjesus.ui.theme.WindowWidth
import com.shannonsumner.namesofjesus.ui.theme.isShortLandscape

/** Width-to-height ratio of the cross artwork. */
internal const val ArtworkAspectRatio = 535.32f / 800f

/** Top and bottom padding around the scrolling title in a short landscape window. */
internal val CompactLandscapePadding = 12.dp

/** Sizes and placement for the curtain at one window size, from the website's CSS. */
internal data class CurtainLayout(
    val titleFontSize: TextUnit,
    val authorFontSize: TextUnit,
    val titleLetterSpacing: TextUnit,
    val titleAuthorGap: Dp,
    val centerContent: Boolean,
    val mobileArtwork: Boolean,
    val compactLandscape: Boolean = false
) {
    /** Space between the cursive “of” and “OUR”. */
    val ofOurGap: Dp get() = 8.dp * (titleFontSize.value / 36f)

    fun artworkWidth(windowWidth: Dp, windowHeight: Dp = Dp.Infinity): Dp {
        val width = if (mobileArtwork) windowWidth * 0.72f else {
            minOf(468.dp, windowWidth * 0.42f)
        }
        return if (compactLandscape) {
            minOf(width, windowHeight * 0.85f * ArtworkAspectRatio)
        } else {
            width
        }
    }

    val titleStyle: TextStyle
        get() = DefaultTextStyle.copy(
            color = TextPrimary,
            fontFamily = Seshat,
            fontSize = titleFontSize,
            fontWeight = FontWeight.Light,
            letterSpacing = titleLetterSpacing,
            lineHeight = titleFontSize * (41f / 36f)
        )

    /** The cursive “The” and “of”. */
    val cursiveTitleStyle: TextStyle
        get() = DefaultTextStyle.copy(
            color = TextPrimary,
            fontFamily = Italianno,
            fontSize = titleFontSize,
            letterSpacing = titleLetterSpacing,
            lineHeight = titleFontSize * (41f / 36f)
        )

    val authorStyle: TextStyle
        get() = DefaultTextStyle.copy(
            color = TextPrimary,
            fontFamily = Seshat,
            fontSize = authorFontSize,
            fontWeight = FontWeight.Light,
            lineHeight = authorFontSize * (34f / 28f),
            textAlign = TextAlign.Center
        )
}

internal fun curtainLayoutForWindow(width: Dp, height: Dp): CurtainLayout {
    val layout = curtainLayoutForWidth(width)
    return if (isShortLandscape(width, height)) {
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

internal fun curtainLayoutForWidth(width: Dp): CurtainLayout = when (WindowWidth.of(width)) {
    WindowWidth.Phone -> CurtainLayout(
        titleFontSize = 36.sp,
        authorFontSize = 28.sp,
        titleLetterSpacing = 1.92.sp,
        titleAuthorGap = 24.dp,
        centerContent = false,
        mobileArtwork = true
    )
    WindowWidth.Tablet -> CurtainLayout(
        titleFontSize = 48.sp,
        authorFontSize = 38.sp,
        titleLetterSpacing = 4.sp,
        titleAuthorGap = 32.dp,
        centerContent = false,
        mobileArtwork = false
    )
    WindowWidth.Large -> CurtainLayout(
        titleFontSize = 60.sp,
        authorFontSize = 48.sp,
        titleLetterSpacing = 6.4.sp,
        titleAuthorGap = 48.dp,
        centerContent = true,
        mobileArtwork = false
    )
}
