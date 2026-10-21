package com.shannonsumner.namesofjesus.ui.dailyreading

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.shannonsumner.namesofjesus.data.RichText
import com.shannonsumner.namesofjesus.data.ScriptureSelection
import com.shannonsumner.namesofjesus.data.TextFormat
import com.shannonsumner.namesofjesus.ui.theme.Accent
import com.shannonsumner.namesofjesus.ui.theme.Georgia
import com.shannonsumner.namesofjesus.ui.theme.PanelBackground
import com.shannonsumner.namesofjesus.ui.theme.ShadowColor
import com.shannonsumner.namesofjesus.ui.theme.StixTwoText
import com.shannonsumner.namesofjesus.ui.theme.TextPrimary
import com.shannonsumner.namesofjesus.ui.theme.WindowWidth

// Visual styling for the daily reading, matching the companion site's DailyReading.css.
// Layout lives in DailyReading.kt and DailyReadingPanel.kt; behavior lives in
// DailyReadingViewModel.kt.

// The translucent panel.
internal val PanelMaxWidth = 960.dp
internal val PanelCornerRadius = 6.dp
internal val PanelShadowElevation = 16.dp
internal val PanelShadowColor = ShadowColor
internal val PanelColor = PanelBackground

/** Space between the screen edge and the panel; smaller on phones and tablets. */
internal fun panelOuterPadding(width: WindowWidth): Dp =
    if (width == WindowWidth.Large) 40.dp else 24.dp

private val BaseFontSize = 22.sp // 1.375rem
private val HeadingFontSize = 40.sp // 2.5rem
private val LineHeight = 1.6.em
private val BoldWeight = FontWeight.SemiBold // 600
private val SmallerFontSize = 0.83.em // browsers' default size for <sup> and <sub>

/** Spreads extra line height evenly above and below each line, as CSS does. */
private val CssLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None
)

@Immutable
internal data class DailyReadingStyle(
    val contentPadding: PaddingValues,
    val headingStyle: TextStyle,
    val headingGap: Dp,
    val scriptureStyle: TextStyle,
    val referenceStyle: SpanStyle,
    val sectionGap: Dp,
    val bodyStyle: TextStyle,
    val activityStyle: TextStyle,
    val paragraphGap: Dp,
    val scriptureLinkStyle: SpanStyle
)

/** Styles for the daily reading; phones (600 dp and narrower) get less side padding. */
internal fun dailyReadingStyle(isCompact: Boolean): DailyReadingStyle =
    if (isCompact) PhoneStyle else RegularStyle

private fun buildDailyReadingStyle(isCompact: Boolean): DailyReadingStyle {
    val bodyStyle = TextStyle(
        color = TextPrimary,
        fontFamily = StixTwoText,
        fontSize = BaseFontSize,
        lineHeight = LineHeight,
        lineHeightStyle = CssLineHeightStyle
    )
    return DailyReadingStyle(
        contentPadding = PaddingValues(
            horizontal = if (isCompact) 24.dp else 48.dp,
            vertical = 24.dp
        ),
        headingStyle = bodyStyle.copy(
            fontFamily = Georgia,
            fontSize = HeadingFontSize,
            fontWeight = BoldWeight,
            textAlign = TextAlign.Center
        ),
        headingGap = 24.dp,
        scriptureStyle = bodyStyle.copy(fontStyle = FontStyle.Italic, textAlign = TextAlign.Start),
        referenceStyle = SpanStyle(
            color = Accent,
            fontStyle = FontStyle.Normal,
            fontWeight = BoldWeight
        ),
        sectionGap = 24.dp,
        bodyStyle = bodyStyle,
        activityStyle = bodyStyle.copy(fontStyle = FontStyle.Italic),
        paragraphGap = 16.dp,
        scriptureLinkStyle = SpanStyle(color = Accent, fontWeight = BoldWeight)
    )
}

private val PhoneStyle = buildDailyReadingStyle(isCompact = true)
private val RegularStyle = buildDailyReadingStyle(isCompact = false)

private fun TextFormat.spanStyle(): SpanStyle = when (this) {
    TextFormat.Bold -> SpanStyle(fontWeight = FontWeight.Bold)
    TextFormat.Italic -> SpanStyle(fontStyle = FontStyle.Italic)
    TextFormat.Underline -> SpanStyle(textDecoration = TextDecoration.Underline)
    TextFormat.Superscript -> SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = SmallerFontSize)
    TextFormat.Subscript -> SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = SmallerFontSize)
}

/** The main scripture followed by its reference, e.g. “For God so loved… (John 3:16)”. */
internal fun scriptureWithReference(scripture: String, reference: String, style: DailyReadingStyle) =
    buildAnnotatedString {
        append(scripture)
        append(' ')
        withStyle(style.referenceStyle) { append("($reference)") }
    }

/**
 * Applies the reading's styles to parsed devotional text. Tapping a scripture
 * reference calls [onScriptureClick].
 */
internal fun RichText.toAnnotatedString(
    style: DailyReadingStyle,
    onScriptureClick: (ScriptureSelection) -> Unit
): AnnotatedString = buildAnnotatedString {
    var index = 0
    while (index < runs.size) {
        val first = runs[index]
        if (!first.isScriptureLink) {
            appendFormatted(first.text, first.formats)
            index++
            continue
        }
        val linkRuns = runs.drop(index).takeWhile {
            it.isScriptureLink && it.scripture == first.scripture
        }
        index += linkRuns.size
        val appendLinkText: AnnotatedString.Builder.() -> Unit = {
            linkRuns.forEach { appendFormatted(it.text, it.formats) }
        }
        val scripture = first.scripture
        if (scripture == null) {
            withStyle(style.scriptureLinkStyle, appendLinkText)
        } else {
            val link = LinkAnnotation.Clickable(
                tag = scripture.reference,
                styles = TextLinkStyles(style = style.scriptureLinkStyle),
                linkInteractionListener = { onScriptureClick(scripture) }
            )
            withLink(link, appendLinkText)
        }
    }
}

private fun AnnotatedString.Builder.appendFormatted(text: String, formats: Set<TextFormat>) {
    if (formats.isEmpty()) {
        append(text)
        return
    }
    val merged = formats.fold(SpanStyle()) { style, format -> style.merge(format.spanStyle()) }
    withStyle(merged) { append(text) }
}
