package com.shannonsumner.namesofjesus.ui.scripture

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.shannonsumner.namesofjesus.R
import com.shannonsumner.namesofjesus.data.ScriptureSelection
import com.shannonsumner.namesofjesus.data.ScriptureVerse
import com.shannonsumner.namesofjesus.ui.theme.WonderfulNamesTheme

/** Shows a tapped passage's KJV text. Back, tapping outside, or Close calls [onDismiss]. */
@Composable
internal fun ScriptureDialog(state: ScriptureDialogState, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ScriptureDialogStyle.ContainerColor,
        title = {
            Text(
                text = state.selection.reference,
                style = ScriptureDialogStyle.Title,
                modifier = Modifier.semantics { heading() }
            )
        },
        text = { PassageBody(state.content) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.scripture_close)) }
        }
    )
}

@Composable
private fun PassageBody(content: PassageContent) {
    when (content) {
        PassageContent.Loading -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth().padding(ScriptureDialogStyle.LoadingPadding)
        ) {
            CircularProgressIndicator()
        }
        PassageContent.Unavailable -> Text(
            text = stringResource(R.string.scripture_unavailable),
            style = ScriptureDialogStyle.Message
        )
        is PassageContent.Loaded -> Column(
            verticalArrangement = Arrangement.spacedBy(ScriptureDialogStyle.VerseSpacing),
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            content.verses.forEach { verse ->
                Text(text = verseText(verse), style = ScriptureDialogStyle.Verse)
            }
        }
    }
}

@Preview
@Composable
private fun ScriptureDialogPreview() {
    WonderfulNamesTheme {
        ScriptureDialog(
            state = ScriptureDialogState(
                selection = ScriptureSelection(43, 3, "16-17", "John 3:16-17"),
                content = PassageContent.Loaded(
                    listOf(
                        ScriptureVerse(16, "‹For God so loved the world, that he gave his only begotten Son…›"),
                        ScriptureVerse(17, "‹For God sent not his Son into the world to condemn the world…›")
                    )
                )
            ),
            onDismiss = {}
        )
    }
}
