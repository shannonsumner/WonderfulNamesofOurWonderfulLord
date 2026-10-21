package com.shannonsumner.namesofjesus.ui.theme

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WindowWidthTest {
    @Test
    fun breakpointsMatchTheWebsite() {
        assertEquals(WindowWidth.Phone, WindowWidth.of(390.dp))
        assertEquals(WindowWidth.Phone, WindowWidth.of(600.dp))
        assertEquals(WindowWidth.Tablet, WindowWidth.of(601.dp))
        assertEquals(WindowWidth.Tablet, WindowWidth.of(1024.dp))
        assertEquals(WindowWidth.Large, WindowWidth.of(1025.dp))
    }

    @Test
    fun shortLandscapeNeedsWiderThanTallAndAtMost600DpTall() {
        assertTrue(isShortLandscape(844.dp, 390.dp))
        assertTrue(isShortLandscape(960.dp, 600.dp))
        assertFalse(isShortLandscape(960.dp, 601.dp))
        assertFalse(isShortLandscape(600.dp, 600.dp))
        assertFalse(isShortLandscape(390.dp, 844.dp))
    }
}
