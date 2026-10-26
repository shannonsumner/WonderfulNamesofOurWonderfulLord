package com.shannonsumner.namesofjesus.ui.share

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.shannonsumner.namesofjesus.R
import com.shannonsumner.namesofjesus.ui.theme.WonderfulNamesTheme

/**
 * The reading's floating buttons, like the website's `ShareButtons.tsx`:
 * copyright info, and share, which sends [shareText] through the system share sheet.
 */
@Composable
internal fun ShareButtons(
    shareText: String,
    title: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isInfoOpen by rememberSaveable { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.spacedBy(ShareButtonsStyle.ButtonGap),
        modifier = modifier
    ) {
        RoundIconButton(
            icon = R.drawable.ic_info,
            label = stringResource(R.string.share_copyright_info),
            onClick = { isInfoOpen = true }
        )
        RoundIconButton(
            icon = R.drawable.ic_share,
            label = stringResource(R.string.share_reading),
            onClick = { shareReading(context, shareText, title) }
        )
    }
    if (isInfoOpen) CopyrightDialog(onDismiss = { isInfoOpen = false })
}

private fun shareReading(context: Context, text: String, title: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TITLE, title)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(send, null))
}

@Composable
private fun RoundIconButton(@DrawableRes icon: Int, label: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isActive = isPressed || isFocused
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(ShareButtonsStyle.ButtonSize)
            .clip(CircleShape)
            .background(
                if (isActive) ShareButtonsStyle.ActiveContainerColor else ShareButtonsStyle.ContainerColor
            )
            .border(ShareButtonsStyle.BorderWidth, ShareButtonsStyle.BorderColor, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            tint = if (isActive) ShareButtonsStyle.ActiveContentColor else ShareButtonsStyle.ContentColor,
            modifier = Modifier.size(ShareButtonsStyle.IconSize)
        )
    }
}

@Preview
@Composable
private fun ShareButtonsPreview() {
    WonderfulNamesTheme { ShareButtons(shareText = "", title = "") }
}
