package com.shannonsumner.namesofjesus

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import org.junit.Rule
import org.junit.Test

class MainScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun curtainWaitsThenRaisesAndStaysDismissedAfterRestoration() {
        compose.mainClock.autoAdvance = false
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            MaterialTheme {
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
