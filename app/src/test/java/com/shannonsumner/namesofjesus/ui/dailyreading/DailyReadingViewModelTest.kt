package com.shannonsumner.namesofjesus.ui.dailyreading

import com.shannonsumner.namesofjesus.data.Devotional
import com.shannonsumner.namesofjesus.data.DevotionalBody
import com.shannonsumner.namesofjesus.data.DevotionalRepository
import com.shannonsumner.namesofjesus.data.ScriptureRepository
import com.shannonsumner.namesofjesus.data.ScriptureSelection
import com.shannonsumner.namesofjesus.data.ScriptureVerse
import com.shannonsumner.namesofjesus.ui.scripture.PassageContent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class DailyReadingViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val start = LocalDate.of(2025, 2, 28)
    private val scripture = ScriptureSelection(43, 3, "16", "John 3:16")

    private fun devotional(date: LocalDate) = Devotional(
        topic = date.toString(),
        scripture = "",
        reference = "",
        mainScripture = null,
        devo = DevotionalBody(emptyList(), false),
        activity = DevotionalBody(emptyList(), false)
    )

    private fun viewModel(
        loadDevotional: suspend (LocalDate) -> Devotional? = { devotional(it) },
        loadPassage: suspend (ScriptureSelection) -> List<ScriptureVerse>? = { null },
        today: () -> LocalDate = { start }
    ) = DailyReadingViewModel(
        devotionals = object : DevotionalRepository {
            override suspend fun getDevotional(date: LocalDate) = loadDevotional(date)
        },
        scriptures = object : ScriptureRepository {
            override suspend fun prepare() = Unit
            override suspend fun getPassage(selection: ScriptureSelection) = loadPassage(selection)
        },
        clock = TestClock(today)
    )

    /** A clock whose date the test can change. */
    private class TestClock(private val today: () -> LocalDate) : Clock() {
        override fun getZone(): ZoneId = ZoneOffset.UTC
        override fun withZone(zone: ZoneId): Clock = this
        override fun instant(): Instant = today().atStartOfDay(ZoneOffset.UTC).toInstant()
    }

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun loadsTodayThenSelectedDatesAndClosesScriptureWhenDateChanges() = runTest(dispatcher) {
        val viewModel = viewModel()
        advanceUntilIdle()
        assertEquals(start.toString(), viewModel.uiState.value.devotional?.topic)

        viewModel.openScripture(scripture)
        assertEquals(scripture, viewModel.uiState.value.scripture?.selection)

        val next = start.plusDays(1)
        viewModel.selectDate(next)
        assertNull(viewModel.uiState.value.scripture)
        advanceUntilIdle()
        assertEquals(next, viewModel.uiState.value.date)
        assertEquals(next.toString(), viewModel.uiState.value.devotional?.topic)
    }

    @Test
    fun returningToTodayUsesTheCurrentDate() = runTest(dispatcher) {
        var now = start
        val viewModel = viewModel(today = { now })
        viewModel.selectDate(start.minusDays(5))
        now = start.plusDays(1)

        viewModel.returnToToday()
        advanceUntilIdle()

        assertEquals(now, viewModel.uiState.value.today)
        assertEquals(now, viewModel.uiState.value.date)
        assertEquals(now.toString(), viewModel.uiState.value.devotional?.topic)
    }

    @Test
    fun aSlowEarlierLoadDoesNotReplaceTheLatestDate() = runTest(dispatcher) {
        val slow = CompletableDeferred<Devotional?>()
        val next = start.plusDays(1)
        val viewModel = viewModel(loadDevotional = { if (it == start) slow.await() else devotional(it) })
        advanceUntilIdle()

        viewModel.selectDate(next)
        slow.complete(devotional(start))
        advanceUntilIdle()

        assertEquals(next.toString(), viewModel.uiState.value.devotional?.topic)
    }

    @Test
    fun failedLoadShowsNoDevotional() = runTest(dispatcher) {
        val viewModel = viewModel(loadDevotional = { if (it == start) devotional(it) else error("database unavailable") })
        advanceUntilIdle()

        viewModel.selectDate(start.plusDays(1))
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.devotional)
    }

    @Test
    fun openingScriptureShowsLoadingThenTheVerses() = runTest(dispatcher) {
        val verses = listOf(ScriptureVerse(16, "For God so loved the world"))
        val viewModel = viewModel(loadPassage = { verses })

        viewModel.openScripture(scripture)
        assertEquals(PassageContent.Loading, viewModel.uiState.value.scripture?.content)
        advanceUntilIdle()
        assertEquals(PassageContent.Loaded(verses), viewModel.uiState.value.scripture?.content)

        viewModel.closeScripture()
        assertNull(viewModel.uiState.value.scripture)
    }

    @Test
    fun missingOrFailedPassagesAreUnavailable() = runTest(dispatcher) {
        val missing = ScriptureSelection(43, 3, "99", "John 3:99")
        val viewModel = viewModel(loadPassage = { if (it == missing) null else error("database unavailable") })

        viewModel.openScripture(missing)
        advanceUntilIdle()
        assertEquals(PassageContent.Unavailable, viewModel.uiState.value.scripture?.content)

        viewModel.openScripture(scripture)
        advanceUntilIdle()
        assertEquals(scripture, viewModel.uiState.value.scripture?.selection)
        assertEquals(PassageContent.Unavailable, viewModel.uiState.value.scripture?.content)
    }

    @Test
    fun aSlowEarlierPassageDoesNotReplaceTheLatestOne() = runTest(dispatcher) {
        val slow = CompletableDeferred<List<ScriptureVerse>?>()
        val first = ScriptureSelection(43, 1, "1", "John 1:1")
        val latest = listOf(ScriptureVerse(16, "For God so loved the world"))
        val viewModel = viewModel(loadPassage = { if (it == first) slow.await() else latest })

        viewModel.openScripture(first)
        advanceUntilIdle()
        viewModel.openScripture(scripture)
        slow.complete(listOf(ScriptureVerse(1, "In the beginning")))
        advanceUntilIdle()

        assertEquals(scripture, viewModel.uiState.value.scripture?.selection)
        assertEquals(PassageContent.Loaded(latest), viewModel.uiState.value.scripture?.content)
    }

    @Test
    fun closingWhileLoadingKeepsThePopupClosed() = runTest(dispatcher) {
        val slow = CompletableDeferred<List<ScriptureVerse>?>()
        val viewModel = viewModel(loadPassage = { slow.await() })

        viewModel.openScripture(scripture)
        advanceUntilIdle()
        viewModel.closeScripture()
        slow.complete(listOf(ScriptureVerse(16, "For God so loved the world")))
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.scripture)
    }
}
