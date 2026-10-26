package com.shannonsumner.namesofjesus.ui.datenav

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.shannonsumner.namesofjesus.data.devotionalDayOfYear
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import com.shannonsumner.namesofjesus.ui.theme.WonderfulNamesTheme

class DateNavTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun previousAndNextButtonsNavigateAcrossMonthBoundary() {
        var date by mutableStateOf(LocalDate.of(2024, 1, 1))
        compose.setContent {
            WonderfulNamesTheme {
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
    fun calendarSelectsImmediatelyAndHidesYearControls() {
        var date by mutableStateOf(LocalDate.of(2024, 2, 1))
        compose.setContent {
            WonderfulNamesTheme {
                DateNav(
                    date = date,
                    onDateChange = { date = it },
                    isCompact = false
                )
            }
        }

        compose.onNodeWithContentDescription("Choose date").performClick()
        compose.onNodeWithText("February").assertExists()
        compose.onAllNodesWithText("Feb 1").assertCountEquals(0)
        compose.onAllNodesWithText("2024").assertCountEquals(0)
        compose.onAllNodesWithText("Select date").assertCountEquals(0)
        compose.onAllNodesWithText("OK").assertCountEquals(0)
        compose.onAllNodesWithText("Cancel").assertCountEquals(0)
        compose.onNodeWithContentDescription("February 29").performClick()

        compose.onAllNodesWithText("February 29").assertCountEquals(1)
        compose.onAllNodesWithText("2024").assertCountEquals(0)
        compose.runOnIdle {
            assertEquals(LocalDate.of(2024, 2, 29), date)
            assertEquals(60, devotionalDayOfYear(date))
        }
    }

    @Test
    fun reopeningStartsOnCurrentDateAndMonthNavigationWorks() {
        var date by mutableStateOf(LocalDate.of(2025, 3, 1))
        compose.setContent {
            WonderfulNamesTheme {
                DateNav(date = date, onDateChange = { date = it }, isCompact = true)
            }
        }

        compose.onNodeWithContentDescription("Choose date").performClick()
        compose.onNodeWithContentDescription("Next month").performClick()
        compose.onNodeWithText("April").assertExists()
        compose.onNodeWithContentDescription("April 15").performClick()
        compose.runOnIdle { assertEquals(LocalDate.of(2025, 4, 15), date) }

        compose.onNodeWithContentDescription("Choose date").performClick()
        compose.onAllNodesWithText("Apr 15").assertCountEquals(0)
        compose.runOnIdle {
            assertEquals(LocalDate.of(2025, 4, 15), date)
            assertEquals(106, devotionalDayOfYear(date))
        }
    }

    @Test
    fun pickerDoesNotExposeYearSelection() {
        var date by mutableStateOf(LocalDate.of(2024, 3, 1))
        compose.setContent {
            WonderfulNamesTheme {
                DateNav(date = date, onDateChange = { date = it }, isCompact = true)
            }
        }

        compose.onNodeWithContentDescription("Choose date").performClick()
        compose.onAllNodes(hasContentDescription("Switch to year selection")).assertCountEquals(0)
        compose.onAllNodesWithText("2024").assertCountEquals(0)
        compose.onNodeWithContentDescription("Previous month").performClick()
        compose.onNodeWithText("February").assertExists()
        compose.onNodeWithContentDescription("February 29").performClick()
        compose.runOnIdle {
            assertEquals(LocalDate.of(2024, 2, 29), date)
        }
        compose.onNodeWithContentDescription("Choose date").performClick()
        compose.onNodeWithContentDescription("February 28").performClick()
        compose.runOnIdle {
            assertEquals(LocalDate.of(2024, 2, 28), date)
            assertEquals(59, devotionalDayOfYear(date))
        }
    }
}
