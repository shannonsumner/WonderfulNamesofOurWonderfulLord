package com.shannonsumner.namesofjesus

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class DailyReadingPanelTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun resumingResetsManuallySelectedDateToToday() {
        val owner = object : LifecycleOwner {
            override val lifecycle = LifecycleRegistry(this)
        }
        compose.runOnIdle {
            owner.lifecycle.currentState = Lifecycle.State.RESUMED
        }
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                MaterialTheme {
                    DailyReadingPanel()
                }
            }
        }

        val today = LocalDate.now()
        val previousDay = today.minusDays(1)
        fun label(date: LocalDate) =
            "${date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${date.dayOfMonth}"

        compose.onAllNodesWithText(label(today)).assertCountEquals(1)
        compose.onNodeWithContentDescription("Previous Day").performClick()
        compose.onAllNodesWithText(label(previousDay)).assertCountEquals(1)

        compose.runOnIdle {
            owner.lifecycle.currentState = Lifecycle.State.CREATED
        }
        compose.runOnIdle {
            owner.lifecycle.currentState = Lifecycle.State.RESUMED
        }

        compose.onAllNodesWithText(label(today)).assertCountEquals(1)
        compose.onAllNodesWithText(label(previousDay)).assertCountEquals(0)
    }
}
