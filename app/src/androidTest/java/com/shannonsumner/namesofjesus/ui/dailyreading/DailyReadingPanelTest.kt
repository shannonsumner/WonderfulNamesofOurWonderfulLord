package com.shannonsumner.namesofjesus.ui.dailyreading

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.shannonsumner.namesofjesus.HiltTestActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import com.shannonsumner.namesofjesus.ui.theme.WonderfulNamesTheme

@HiltAndroidTest
class DailyReadingPanelTest {
    @get:Rule(order = 0)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createAndroidComposeRule<HiltTestActivity>()

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
                WonderfulNamesTheme {
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
