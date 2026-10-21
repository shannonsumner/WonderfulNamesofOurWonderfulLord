package com.shannonsumner.namesofjesus.ui.dailyreading

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.font.FontStyle
import com.shannonsumner.namesofjesus.data.ScriptureSelection
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import com.shannonsumner.namesofjesus.ui.theme.WonderfulNamesTheme

class DailyReadingTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun showsDevotionalAndOpensItsScriptures() {
        val opened = mutableListOf<ScriptureSelection>()
        compose.setContent {
            WonderfulNamesTheme {
                DailyReading(PreviewDevotional, isCompact = true, onScriptureClick = { opened += it })
            }
        }

        compose.onNodeWithText("The Seed of the Woman").assertIsDisplayed()
        compose.onNodeWithText("(Genesis 3:15)", substring = true).performClick()
        compose.onNodeWithText("Oh, Thou who didst humble", substring = true).assertIsDisplayed()
        compose.onNodeWithText("     And up from the rocky steep,", substring = true).assertExists()

        compose.runOnIdle {
            assertEquals(listOf(ScriptureSelection(1, 3, "15", "Genesis 3:15")), opened)
        }
    }

    @Test
    fun inlineScriptureReferencesAreStyledLinks() {
        val opened = mutableListOf<ScriptureSelection>()
        lateinit var style: DailyReadingStyle
        compose.setContent { style = dailyReadingStyle(isCompact = true) }

        val text = compose.runOnIdle {
            PreviewDevotional.devo.paragraphs.single().toAnnotatedString(style) { opened += it }
        }
        val link = text.getLinkAnnotations(0, text.length).single()
        assertEquals("Philippians 2:9", text.substring(link.start, link.end))
        val clickable = link.item as LinkAnnotation.Clickable
        assertEquals(style.scriptureLinkStyle, clickable.styles?.style)

        clickable.linkInteractionListener?.onClick(clickable)
        assertEquals(listOf(ScriptureSelection(50, 2, "9", "Philippians 2:9")), opened)

        val italic = text.spanStyles.filter { it.item.fontStyle == FontStyle.Italic }
            .map { text.substring(it.start, it.end) }
        assertEquals(listOf("\"Seed of the Woman.\"", "All through the mountains, thunder-riven,\n     And up from the rocky steep,"), italic)
    }
}
