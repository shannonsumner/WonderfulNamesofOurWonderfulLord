package com.shannonsumner.namesofjesus.ui.dailyreading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shannonsumner.namesofjesus.data.DevotionalRepository
import com.shannonsumner.namesofjesus.data.Devotional
import com.shannonsumner.namesofjesus.data.ScriptureRepository
import com.shannonsumner.namesofjesus.data.ScriptureSelection
import com.shannonsumner.namesofjesus.ui.scripture.PassageContent
import com.shannonsumner.namesofjesus.ui.scripture.ScriptureDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

/** Everything the daily reading screen shows. */
data class DailyReadingUiState(
    val today: LocalDate,
    val date: LocalDate,
    /** The loaded devotional; the previous day's stays visible until the next one loads. */
    val devotional: Devotional? = null,
    /** The passage popup, or null when no passage is open. */
    val scripture: ScriptureDialogState? = null
)

/**
 * Holds the selected date, loads its devotional, and loads tapped passages. Contains no UI code.
 * Hilt supplies the constructor arguments (see `di/DataModule.kt`).
 */
@HiltViewModel
class DailyReadingViewModel @Inject constructor(
    private val devotionals: DevotionalRepository,
    private val scriptures: ScriptureRepository,
    private val clock: Clock
) : ViewModel() {
    private val _uiState = currentDate().let { MutableStateFlow(DailyReadingUiState(today = it, date = it)) }
    val uiState: StateFlow<DailyReadingUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var passageJob: Job? = null

    init {
        load(_uiState.value.date)
        // The KJV database is built on first use; start it now so the first popup opens quickly.
        viewModelScope.launch { loadSafely { scriptures.prepare() } }
    }

    fun selectDate(date: LocalDate) {
        if (date == _uiState.value.date) return
        passageJob?.cancel()
        _uiState.update { it.copy(date = date, scripture = null) }
        load(date)
    }

    /** Called when the app returns to the foreground: moves to the current day. */
    fun returnToToday() {
        val today = currentDate()
        _uiState.update { it.copy(today = today) }
        selectDate(today)
    }

    /** Opens the passage popup for [selection] and loads its verses. */
    fun openScripture(selection: ScriptureSelection) {
        passageJob?.cancel()
        val dialog = ScriptureDialogState(selection)
        _uiState.update { it.copy(scripture = dialog) }
        passageJob = viewModelScope.launch {
            val verses = loadSafely { scriptures.getPassage(selection) }
            val content = verses?.takeIf { it.isNotEmpty() }
                ?.let { PassageContent.Loaded(it) }
                ?: PassageContent.Unavailable
            _uiState.update {
                if (it.scripture === dialog) it.copy(scripture = dialog.copy(content = content)) else it
            }
        }
    }

    fun closeScripture() {
        passageJob?.cancel()
        _uiState.update { it.copy(scripture = null) }
    }

    private fun load(date: LocalDate) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val devotional = loadSafely { devotionals.getDevotional(date) }
            _uiState.update { if (it.date == date) it.copy(devotional = devotional) else it }
        }
    }

    private fun currentDate(): LocalDate = LocalDate.now(clock)

    /** Runs [load], treating any failure other than cancellation as "not found". */
    private suspend fun <T> loadSafely(load: suspend () -> T?): T? = try {
        load()
    } catch (exception: CancellationException) {
        throw exception
    } catch (_: Exception) {
        null
    }
}
