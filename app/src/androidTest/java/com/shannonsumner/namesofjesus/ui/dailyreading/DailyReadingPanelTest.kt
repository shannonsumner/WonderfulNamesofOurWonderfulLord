package com.shannonsumner.namesofjesus.ui.dailyreading

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.shannonsumner.namesofjesus.HiltTestActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.swipeUp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import com.shannonsumner.namesofjesus.data.DevotionalHtml
import com.shannonsumner.namesofjesus.ui.theme.WonderfulNamesTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@HiltAndroidTest
class DailyReadingPanelTest {
    @get:Rule(order = 0)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createAndroidComposeRule<HiltTestActivity>()

    @Test
    fun swipeLeftMatchesNextArrowAndSwipeRightMatchesPreviousArrow() {
        val start = LocalDate.of(2026, 10, 22)
        val selectedDates = showSwipePanel(start)

        compose.onNodeWithTag("readingScreen").performTouchInput { swipeLeft() }
        compose.onNodeWithContentDescription("Previous Day").performClick()
        compose.onNodeWithContentDescription("Next Day").performClick()
        compose.onNodeWithTag("readingScreen").performTouchInput { swipeRight() }

        compose.runOnIdle {
            assertEquals(listOf(start.plusDays(1), start, start.plusDays(1), start), selectedDates)
        }
    }

    @Test
    fun horizontalSwipesMoveOneDayAndCrossYearBoundary() {
        val start = LocalDate.of(2026, 1, 1)
        val selectedDates = showSwipePanel(start)

        compose.onNodeWithTag("readingScreen").performTouchInput { swipeRight() }
        compose.onNodeWithText(start.minusDays(1).toString()).assertExists()
        compose.onNodeWithTag("readingScreen").performTouchInput { swipeLeft() }
        compose.onNodeWithText(start.toString()).assertExists()
        compose.onNodeWithTag("readingScreen").performTouchInput { swipeLeft() }
        compose.onNodeWithText(start.plusDays(1).toString()).assertExists()

        compose.runOnIdle {
            assertEquals(listOf(start.minusDays(1), start, start.plusDays(1)), selectedDates)
        }
    }

    @Test
    fun swipesNavigateThroughLeapDay() {
        val start = LocalDate.of(2024, 2, 28)
        val selectedDates = showSwipePanel(start)

        compose.onNodeWithTag("readingScreen").performTouchInput { swipeLeft() }
        compose.onNodeWithText("2024-02-29").assertExists()
        compose.onNodeWithTag("readingScreen").performTouchInput { swipeLeft() }
        compose.onNodeWithText("2024-03-01").assertExists()
        compose.onNodeWithTag("readingScreen").performTouchInput { swipeRight() }
        compose.onNodeWithText("2024-02-29").assertExists()

        compose.runOnIdle {
            assertEquals(listOf(start.plusDays(1), start.plusDays(2), start.plusDays(1)), selectedDates)
        }
    }

    @Test
    fun shortAndCanceledDragsDoNotChangeDate() {
        val selectedDates = showSwipePanel(LocalDate.of(2026, 10, 22))
        compose.onNodeWithTag("readingScreen").performTouchInput {
            down(center)
            moveBy(Offset(30f, 0f))
            up()
        }
        compose.onNodeWithTag("readingScreen").performTouchInput {
            down(center)
            moveBy(Offset(-width / 2f, 0f))
            cancel()
        }

        compose.runOnIdle { assertTrue(selectedDates.isEmpty()) }
    }

    @Test
    fun verticalSwipeScrollsWithoutChangingDateAndNewDayStartsAtTop() {
        val start = LocalDate.of(2026, 10, 22)
        val selectedDates = showSwipePanel(start)
        val reading = compose.onNode(hasScrollAction())

        reading.performTouchInput { swipeUp() }
        compose.runOnIdle { assertTrue(selectedDates.isEmpty()) }
        assertTrue(
            reading.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value() > 0f
        )

        compose.onNodeWithTag("readingScreen").performTouchInput { swipeLeft() }
        compose.onNodeWithText(start.plusDays(1).toString()).assertExists()
        assertEquals(
            0f,
            reading.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value()
        )
    }

    @Test
    fun shareButtonsStayVisibleWhileScrollingAndInfoShowsCopyright() {
        showSwipePanel(LocalDate.of(2026, 10, 22))
        val share = compose.onNodeWithContentDescription("Share reading")
        val info = compose.onNodeWithContentDescription("Copyright info")
        share.assertIsDisplayed()
        info.assertIsDisplayed()

        compose.onNode(hasScrollAction()).performTouchInput { swipeUp() }
        share.assertIsDisplayed()
        info.assertIsDisplayed()

        info.performClick()
        compose.onNodeWithText("Copyright Info").assertIsDisplayed()
        compose.onNodeWithText("Barbour Publishing", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Close").performClick()
        compose.onNodeWithText("Copyright Info").assertDoesNotExist()
    }

    @Test
    fun readingScrollsPastShareButtons() {
        showSwipePanel(LocalDate.of(2026, 10, 22))
        compose.onNode(hasScrollAction()).performSemanticsAction(SemanticsActions.ScrollBy) {
            it(0f, 100_000f)
        }

        val lastLine = compose.onNodeWithText("Amen.", substring = true).getUnclippedBoundsInRoot()
        val buttons = compose.onNodeWithContentDescription("Share reading").getUnclippedBoundsInRoot()
        assertTrue(lastLine.bottom <= buttons.top)
    }

    private fun showSwipePanel(start: LocalDate): MutableList<LocalDate> {
        val selectedDates = mutableListOf<LocalDate>()
        val body = DevotionalHtml.parseBody(
            List(40) { "A paragraph of devotional text for scrolling." }.joinToString("\n\n")
        )
        compose.setContent {
            var date by remember { mutableStateOf(start) }
            WonderfulNamesTheme {
                DailyReadingPanel(
                    uiState = DailyReadingUiState(
                        today = start,
                        date = date,
                        devotional = PreviewDevotional.copy(topic = date.toString(), devo = body)
                    ),
                    onDateChange = {
                        selectedDates.add(it)
                        date = it
                    },
                    onScriptureClick = {},
                    onScriptureDismiss = {},
                    modifier = Modifier.testTag("readingScreen")
                )
            }
        }
        return selectedDates
    }

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
