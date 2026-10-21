package com.shannonsumner.namesofjesus.ui.theme

import androidx.compose.ui.graphics.Color

// The app's palette, matching the companion website's CSS.
// `curtain_background` is also in res/values/colors.xml because the XML splash
// screen and launcher icon need it; keep the two values in sync.

/** Page and curtain background (`--bg-base-color`). */
val CurtainGreen = Color(0xFFB4D77D)

/** Darker vertical stripes over the page background (`--bg-stripe-color`). */
val StripeShade = Color.Black.copy(alpha = 0.06f)

/** Translucent reading panel (`--daily-reading-panel-bg`). */
val PanelBackground = Color(0xBFF5F7EB)

/** Links, references, and date controls (`--daily-reading-accent-color`). */
val Accent = Color(0xFF1F6F52)

/** Highlight behind the selected calendar day. */
val SelectedDayBackground = Color(0xFFF5F7EB)

/** Line under the date controls. */
val Divider = Color.Black.copy(alpha = 0.1f)

val TextPrimary = Color.Black
val TextSecondary = Color.Black.copy(alpha = 0.6f)
val PopupBackground = Color.White

/** Shadow color; Android lowers shadow opacity itself, so black ends up close to the site's. */
val ShadowColor = Color.Black

/** Soft gradient under the rising curtain (28% black). */
val CurtainShadow = Color.Black.copy(alpha = 0.28f)
