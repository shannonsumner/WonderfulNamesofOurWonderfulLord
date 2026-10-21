package com.shannonsumner.namesofjesus.ui.dailyreading

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.shannonsumner.namesofjesus.R
import com.shannonsumner.namesofjesus.data.Devotional
import com.shannonsumner.namesofjesus.data.DevotionalBody
import com.shannonsumner.namesofjesus.data.DevotionalHtml
import com.shannonsumner.namesofjesus.data.ScriptureSelection
import com.shannonsumner.namesofjesus.ui.theme.WonderfulNamesTheme

/**
 * Lays out one day's devotional: topic, main scripture, reading, and closing prayer.
 * Styling comes from [dailyReadingStyle]; this file only arranges content.
 */
@Composable
internal fun DailyReading(
    devotional: Devotional,
    isCompact: Boolean,
    onScriptureClick: (ScriptureSelection) -> Unit,
    modifier: Modifier = Modifier
) {
    val style = dailyReadingStyle(isCompact)
    val currentOnScriptureClick by rememberUpdatedState(onScriptureClick)
    val openScripture: (ScriptureSelection) -> Unit = remember { { currentOnScriptureClick(it) } }

    Column(modifier = modifier.padding(style.contentPadding)) {
        Text(
            text = devotional.topic,
            style = style.headingStyle,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { heading() }
        )
        Spacer(Modifier.height(style.headingGap))
        MainScripture(devotional, style, openScripture)
        Spacer(Modifier.height(style.sectionGap))
        DevotionalBodyText(devotional.devo, style.bodyStyle, style, openScripture)
        DevotionalBodyText(devotional.activity, style.activityStyle, style, openScripture)
    }
}

@Composable
private fun MainScripture(
    devotional: Devotional,
    style: DailyReadingStyle,
    onScriptureClick: (ScriptureSelection) -> Unit
) {
    val text = remember(devotional.scripture, devotional.reference, style) {
        scriptureWithReference(devotional.scripture, devotional.reference, style)
    }
    val scripture = devotional.mainScripture
    val clickLabel = stringResource(R.string.daily_reading_open_scripture, devotional.reference)
    Text(
        text = text,
        style = style.scriptureStyle,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (scripture == null) {
                    Modifier
                } else {
                    Modifier.clickable(
                        onClickLabel = clickLabel,
                        role = Role.Button,
                        onClick = { onScriptureClick(scripture) }
                    )
                }
            )
    )
}

@Composable
private fun DevotionalBodyText(
    body: DevotionalBody,
    textStyle: TextStyle,
    style: DailyReadingStyle,
    onScriptureClick: (ScriptureSelection) -> Unit
) {
    body.paragraphs.forEach { paragraph ->
        val text = remember(paragraph, style) { paragraph.toAnnotatedString(style, onScriptureClick) }
        Text(text = text, style = textStyle, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(style.paragraphGap))
    }
}

internal val PreviewDevotional = Devotional(
    topic = "The Seed of the Woman",
    scripture = "And I will put enmity between thee and the woman, and between thy seed and her seed.",
    reference = "Genesis 3:15",
    mainScripture = ScriptureSelection(1, 3, "15", "Genesis 3:15"),
    devo = DevotionalHtml.parseBody(
        "\"Because He stooped so low God hath exalted Him very high\" " +
            "(<button data-scripture=\"50:2:9\">Philippians 2:9</button>; Arthur Way's Translation). " +
            "He came from the bosom of the Father to become the <em>\"Seed of the Woman.\"</em>\n\n" +
            "<em>All through the mountains, thunder-riven,\n     And up from the rocky steep,</em>"
    ),
    activity = DevotionalHtml.parseBody(
        "Oh, Thou who didst humble Thyself to be born of a woman, we worship and adore. Amen."
    )
)

@Preview(name = "Phone", widthDp = 390, heightDp = 1200, showBackground = true)
@Composable
private fun DailyReadingPhonePreview() {
    WonderfulNamesTheme { DailyReading(PreviewDevotional, isCompact = true, onScriptureClick = {}) }
}

@Preview(name = "Tablet", widthDp = 800, heightDp = 1000, showBackground = true)
@Composable
private fun DailyReadingTabletPreview() {
    WonderfulNamesTheme { DailyReading(PreviewDevotional, isCompact = false, onScriptureClick = {}) }
}
