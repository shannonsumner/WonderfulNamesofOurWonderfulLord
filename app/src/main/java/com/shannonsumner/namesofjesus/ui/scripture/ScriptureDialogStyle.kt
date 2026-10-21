package com.shannonsumner.namesofjesus.ui.scripture

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.shannonsumner.namesofjesus.data.ScriptureVerse
import com.shannonsumner.namesofjesus.ui.theme.DefaultTextStyle
import com.shannonsumner.namesofjesus.ui.theme.Inter
import com.shannonsumner.namesofjesus.ui.theme.PopupBackground
import com.shannonsumner.namesofjesus.ui.theme.TextPrimary

// A standard Material dialog using the app's fonts and colors. Values loosely
// follow the website's Modal.css and ScriptureModal.css.
internal object ScriptureDialogStyle {
    val ContainerColor = PopupBackground
    val VerseSpacing = 12.dp
    val LoadingPadding = 24.dp

    val Title = DefaultTextStyle.copy(
        color = TextPrimary,
        fontFamily = Inter,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
    val Verse = DefaultTextStyle.copy(
        color = TextPrimary,
        fontFamily = Inter,
        fontSize = 16.sp,
        lineHeight = 1.6.em
    )
    val Message = Verse

    /** The superscript verse number, like the website's `<sup>`. */
    val VerseNumber = SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 0.83.em)
}

/** A verse with its superscript number, e.g. “¹⁶For God so loved…”. */
internal fun verseText(verse: ScriptureVerse): AnnotatedString = buildAnnotatedString {
    withStyle(ScriptureDialogStyle.VerseNumber) { append(verse.verse.toString()) }
    append('\u200A') // hair space, like the site's 2px gap after <sup>
    append(verse.text)
}
