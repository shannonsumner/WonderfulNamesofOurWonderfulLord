package com.shannonsumner.namesofjesus.ui.share

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import com.shannonsumner.namesofjesus.R
import com.shannonsumner.namesofjesus.ui.theme.WonderfulNamesTheme

/** The website's `CopyrightModal` as a Material dialog. */
@Composable
internal fun CopyrightDialog(onDismiss: () -> Unit) {
    val paragraphs = listOf(
        R.string.copyright_book,
        R.string.copyright_kjv,
        R.string.copyright_asv
    ).map { AnnotatedString.fromHtml(stringResource(it)) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CopyrightDialogStyle.ContainerColor,
        title = {
            Text(
                text = stringResource(R.string.copyright_title),
                style = CopyrightDialogStyle.Title,
                modifier = Modifier.semantics { heading() }
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(CopyrightDialogStyle.ParagraphGap),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                paragraphs.forEach { Text(text = it, style = CopyrightDialogStyle.Text) }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.scripture_close)) }
        }
    )
}

@Preview
@Composable
private fun CopyrightDialogPreview() {
    WonderfulNamesTheme { CopyrightDialog(onDismiss = {}) }
}
