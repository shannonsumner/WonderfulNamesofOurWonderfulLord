package com.shannonsumner.namesofjesus.ui.scripture

import com.shannonsumner.namesofjesus.data.ScriptureSelection
import com.shannonsumner.namesofjesus.data.ScriptureVerse

/** What the passage popup shows while it is open. */
sealed interface PassageContent {
    data object Loading : PassageContent
    data class Loaded(val verses: List<ScriptureVerse>) : PassageContent
    /** The reference is invalid or its verses couldn't be read. */
    data object Unavailable : PassageContent
}

/** An open passage popup: the tapped reference and its text. */
data class ScriptureDialogState(
    val selection: ScriptureSelection,
    val content: PassageContent = PassageContent.Loading
)
