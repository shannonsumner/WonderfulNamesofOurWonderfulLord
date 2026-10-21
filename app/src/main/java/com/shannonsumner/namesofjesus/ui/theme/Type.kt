package com.shannonsumner.namesofjesus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.shannonsumner.namesofjesus.R

// Font families used across the app. Text styles that use them live in each
// screen's *Style.kt file, the way each web component has its own CSS file.

/**
 * Declares a variable font at a specific [weight]. Without explicit variation
 * settings Android renders every weight of a variable font at its default (400).
 */
@OptIn(ExperimentalTextApi::class)
private fun variableFont(resource: Int, weight: FontWeight, style: FontStyle = FontStyle.Normal) = Font(
    resId = resource,
    weight = weight,
    style = style,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

private val VariableWeights = listOf(FontWeight.Normal, FontWeight.SemiBold, FontWeight.Bold)

/** Curtain title and author. */
val Seshat = FontFamily(Font(R.font.seshat, FontWeight.Normal))

/** The cursive “The” and “of” on the curtain. */
val Italianno = FontFamily(Font(R.font.italianno, FontWeight.Normal))

/** Date controls and other interface text. */
val Inter = FontFamily(
    VariableWeights.flatMap { weight ->
        listOf(
            variableFont(R.font.inter, weight),
            variableFont(R.font.inter_italic, weight, FontStyle.Italic)
        )
    }
)

/** Devotional reading text. */
val StixTwoText = FontFamily(
    VariableWeights.flatMap { weight ->
        listOf(
            variableFont(R.font.stix_two, weight),
            variableFont(R.font.stix_two_italic, weight, FontStyle.Italic)
        )
    }
)

/** Devotional headings. */
val Georgia = FontFamily(Font(R.font.georgia, FontWeight.Normal))

/**
 * Material's default body style, which [androidx.compose.material3.Text] uses when
 * no style is given. Screen styles build on it so they keep its letter spacing and
 * line height.
 */
val DefaultTextStyle: TextStyle = Typography().bodyLarge
