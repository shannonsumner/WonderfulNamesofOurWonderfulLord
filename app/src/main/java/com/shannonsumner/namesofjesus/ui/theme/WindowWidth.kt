package com.shannonsumner.namesofjesus.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Window width ranges, using the companion website's CSS breakpoints. */
enum class WindowWidth {
    /** Up to 600 dp wide. */
    Phone,

    /** 601–1024 dp wide. */
    Tablet,

    /** Wider than 1024 dp. */
    Large;

    companion object {
        fun of(width: Dp): WindowWidth = when {
            width <= 600.dp -> Phone
            width <= 1024.dp -> Tablet
            else -> Large
        }
    }
}

/** A landscape window no more than 600 dp tall, such as a phone on its side. */
fun isShortLandscape(width: Dp, height: Dp): Boolean = width > height && height <= 600.dp
