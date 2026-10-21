package com.shannonsumner.namesofjesus.ui.components

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shannonsumner.namesofjesus.ui.theme.WindowWidth

/**
 * Stripe geometry from the website's background gradient: each stripe repeats
 * every [period], and the darker part runs from [shadeStart] to the end of the period.
 */
internal data class StripeStyle(val shadeStart: Dp, val period: Dp)

internal fun stripeStyleFor(width: WindowWidth): StripeStyle = when (width) {
    WindowWidth.Phone -> StripeStyle(shadeStart = 14.dp, period = 24.dp)
    WindowWidth.Tablet -> StripeStyle(shadeStart = 20.dp, period = 34.dp)
    WindowWidth.Large -> StripeStyle(shadeStart = 30.dp, period = 50.dp)
}
