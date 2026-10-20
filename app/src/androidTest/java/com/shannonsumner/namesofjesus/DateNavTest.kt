package com.shannonsumner.namesofjesus

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class DateNavTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun previousAndNextButtonsNavigateAcrossMonthBoundary() {
        var date by mutableStateOf(LocalDate.of(2024, 1, 1))
        compose.setContent {
            MaterialTheme {
                DateNav(
                    date = date,
                    onDateChange = { date = it },
                    isCompact = true
                )
            }
        }

        compose.onAllNodesWithText("January 1").assertCountEquals(1)
        compose.onNodeWithContentDescription("Previous Day").performClick()
        compose.onAllNodesWithText("December 31").assertCountEquals(1)
        compose.onNodeWithContentDescription("Next Day").performClick()
        compose.onAllNodesWithText("January 1").assertCountEquals(1)
        compose.onNodeWithContentDescription("Next Day").performClick()
        compose.onAllNodesWithText("January 2").assertCountEquals(1)
        compose.onAllNodesWithText("Previous Day").assertCountEquals(0)
    }

    @Test
    fun calendarSelectsDayAndDoesNotDisplayYear() {
        var date by mutableStateOf(LocalDate.of(2024, 1, 31))
        compose.setContent {
            MaterialTheme {
                DateNav(
                    date = date,
                    onDateChange = { date = it },
                    isCompact = false
                )
            }
        }

        compose.onNodeWithContentDescription("Choose date").performClick()
        compose.onNodeWithContentDescription("Next month").performClick()
        compose.onNodeWithContentDescription("February 29").performClick()

        compose.onAllNodesWithText("February 29").assertCountEquals(1)
        compose.onAllNodesWithText("2024").assertCountEquals(0)
    }
}
