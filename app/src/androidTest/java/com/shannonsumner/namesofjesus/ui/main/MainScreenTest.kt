package com.shannonsumner.namesofjesus.ui.main

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.shannonsumner.namesofjesus.HiltTestActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import androidx.compose.ui.test.onAllNodesWithText
import org.junit.Rule
import org.junit.Test
import com.shannonsumner.namesofjesus.ui.theme.WonderfulNamesTheme

@HiltAndroidTest
class MainScreenTest {
    @get:Rule(order = 0)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createAndroidComposeRule<HiltTestActivity>()

    @Test
    fun curtainWaitsThenRaisesAndStaysDismissedAfterRestoration() {
        compose.mainClock.autoAdvance = false
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            WonderfulNamesTheme {
                MainScreen()
            }
        }

        compose.onAllNodesWithText("WONDERFUL").assertCountEquals(2)
        compose.mainClock.advanceTimeBy(900)
        compose.onAllNodesWithText("WONDERFUL").assertCountEquals(2)
        compose.mainClock.advanceTimeBy(2000)
        compose.onAllNodesWithText("WONDERFUL").assertCountEquals(2)
        compose.mainClock.advanceTimeBy(1800)
        compose.onAllNodesWithText("WONDERFUL").assertCountEquals(0)

        restoration.emulateSavedInstanceStateRestore()
        compose.onAllNodesWithText("WONDERFUL").assertCountEquals(0)
    }
}
