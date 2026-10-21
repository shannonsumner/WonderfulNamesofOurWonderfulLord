package com.shannonsumner.namesofjesus.ui.components

import androidx.compose.ui.unit.dp
import com.shannonsumner.namesofjesus.ui.theme.WindowWidth
import org.junit.Assert.assertEquals
import org.junit.Test

class StripedBackgroundStyleTest {
    @Test
    fun stripesWidenWithTheWindow() {
        assertEquals(StripeStyle(shadeStart = 14.dp, period = 24.dp), stripeStyleFor(WindowWidth.Phone))
        assertEquals(StripeStyle(shadeStart = 20.dp, period = 34.dp), stripeStyleFor(WindowWidth.Tablet))
        assertEquals(StripeStyle(shadeStart = 30.dp, period = 50.dp), stripeStyleFor(WindowWidth.Large))
    }
}
