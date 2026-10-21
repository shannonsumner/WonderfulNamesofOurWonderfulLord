package com.shannonsumner.namesofjesus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.shannonsumner.namesofjesus.ui.theme.CurtainGreen
import com.shannonsumner.namesofjesus.ui.theme.StripeShade
import com.shannonsumner.namesofjesus.ui.theme.WindowWidth

/** The green, vertically striped background shared by the curtain and the daily reading page. */
@Composable
internal fun StripedBackground(width: WindowWidth, modifier: Modifier = Modifier) {
    val style = stripeStyleFor(width)
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(CurtainGreen)
        val period = style.period.toPx()
        val shadeStart = style.shadeStart.toPx()
        val shadeWidth = period - shadeStart
        var x = 0f
        while (x < size.width) {
            drawRect(
                color = StripeShade,
                topLeft = Offset(x + shadeStart, 0f),
                size = Size(shadeWidth, size.height)
            )
            x += period
        }
    }
}
