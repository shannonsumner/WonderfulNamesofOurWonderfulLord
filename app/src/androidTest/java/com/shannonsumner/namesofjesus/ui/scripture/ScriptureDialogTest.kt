package com.shannonsumner.namesofjesus.ui.scripture

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.shannonsumner.namesofjesus.data.ScriptureSelection
import com.shannonsumner.namesofjesus.data.ScriptureVerse
import com.shannonsumner.namesofjesus.ui.theme.WonderfulNamesTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ScriptureDialogTest {
    @get:Rule
    val compose = createComposeRule()

    private val selection = ScriptureSelection(43, 3, "16-17", "John 3:16-17")

    @Test
    fun showsReferenceAndNumberedVersesAndCloses() {
        var dismissed = 0
        compose.setContent {
            WonderfulNamesTheme {
                ScriptureDialog(
                    ScriptureDialogState(
                        selection,
                        PassageContent.Loaded(
                            listOf(ScriptureVerse(16, "For God so loved"), ScriptureVerse(17, "For God sent not"))
                        )
                    ),
                    onDismiss = { dismissed++ }
                )
            }
        }

        compose.onNodeWithText("John 3:16-17").assertIsDisplayed()
        compose.onNodeWithText("16\u200AFor God so loved").assertIsDisplayed()
        compose.onNodeWithText("17\u200AFor God sent not").assertIsDisplayed()
        compose.onNodeWithText("Close").performClick()
        compose.runOnIdle { assertEquals(1, dismissed) }
    }

    @Test
    fun showsMessageWhenPassageIsUnavailable() {
        var content: PassageContent by mutableStateOf(PassageContent.Loading)
        compose.setContent {
            WonderfulNamesTheme { ScriptureDialog(ScriptureDialogState(selection, content), onDismiss = {}) }
        }

        compose.onNodeWithText("John 3:16-17").assertIsDisplayed()
        content = PassageContent.Unavailable
        compose.onNodeWithText("This passage couldn’t be loaded.").assertIsDisplayed()
    }
}
