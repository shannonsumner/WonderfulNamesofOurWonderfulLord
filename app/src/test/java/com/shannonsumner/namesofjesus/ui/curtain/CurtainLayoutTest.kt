package com.shannonsumner.namesofjesus.ui.curtain

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CurtainLayoutTest {
    @Test
    fun shortLandscapeUsesCompactTextAndHeightLimitedArtwork() {
        for ((width, height) in listOf(960.dp to 600.dp, 844.dp to 390.dp)) {
            val layout = curtainLayoutForWindow(width, height)
            assertTrue(layout.compactLandscape)
            assertFalse(layout.centerContent)
            assertEquals(36.sp, layout.titleFontSize)
            assertEquals(28.sp, layout.authorFontSize)
            assertEquals(1.92.sp, layout.titleLetterSpacing)
            assertEquals(12.dp, layout.titleAuthorGap)
            val artworkHeight = layout.artworkWidth(width, height) * (800f / 535.32f)
            assertTrue(artworkHeight.value <= height.value * 0.85f + 0.001f)
        }
    }

    @Test
    fun portraitSquareAndTallLandscapeKeepExistingLayouts() {
        for ((width, height) in listOf(
            390.dp to 844.dp,
            600.dp to 960.dp,
            600.dp to 600.dp,
            960.dp to 601.dp,
            1280.dp to 800.dp
        )) {
            assertEquals(
                curtainLayoutForWidth(width),
                curtainLayoutForWindow(width, height)
            )
        }
    }

    @Test
    fun mobileIncludes600DpBreakpoint() {
        for (width in listOf(390.dp, 600.dp)) {
            val layout = curtainLayoutForWidth(width)
            assertEquals(36.sp, layout.titleFontSize)
            assertEquals(28.sp, layout.authorFontSize)
            assertEquals(1.92.sp, layout.titleLetterSpacing)
            assertEquals(24.dp, layout.titleAuthorGap)
            assertFalse(layout.centerContent)
            assertEquals((width * 0.72f).value, layout.artworkWidth(width).value, 0.001f)
        }
    }

    @Test
    fun tabletIncludes1024DpBreakpoint() {
        for (width in listOf(601.dp, 800.dp, 1024.dp)) {
            val layout = curtainLayoutForWidth(width)
            assertEquals(48.sp, layout.titleFontSize)
            assertEquals(38.sp, layout.authorFontSize)
            assertEquals(4.sp, layout.titleLetterSpacing)
            assertEquals(32.dp, layout.titleAuthorGap)
            assertFalse(layout.centerContent)
            assertEquals((width * 0.42f).value, layout.artworkWidth(width).value, 0.001f)
        }
    }

    @Test
    fun largeWindowCentersContentAndCapsArtworkWidth() {
        for (width in listOf(1025.dp, 1200.dp, 1600.dp)) {
            val layout = curtainLayoutForWidth(width)
            assertEquals(60.sp, layout.titleFontSize)
            assertEquals(48.sp, layout.authorFontSize)
            assertEquals(6.4.sp, layout.titleLetterSpacing)
            assertEquals(48.dp, layout.titleAuthorGap)
            assertTrue(layout.centerContent)
            assertEquals(
                minOf(468.dp, width * 0.42f).value,
                layout.artworkWidth(width).value,
                0.001f
            )
        }
    }
}
