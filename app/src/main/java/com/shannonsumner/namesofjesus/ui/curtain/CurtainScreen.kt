package com.shannonsumner.namesofjesus.ui.curtain

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shannonsumner.namesofjesus.R
import com.shannonsumner.namesofjesus.ui.components.StripedBackground
import com.shannonsumner.namesofjesus.ui.theme.WindowWidth
import com.shannonsumner.namesofjesus.ui.theme.WonderfulNamesTheme

@Composable
fun CurtainScreen(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val layout = curtainLayoutForWindow(maxWidth, maxHeight)
        StripedBackground(WindowWidth.of(maxWidth))
        Image(
            painter = painterResource(R.drawable.curtain_background),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(layout.artworkWidth(maxWidth, maxHeight))
                .aspectRatio(ArtworkAspectRatio)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(layout.titleAuthorGap),
            modifier = Modifier
                .align(
                    if (layout.centerContent) Alignment.Center else Alignment.TopCenter
                )
                .then(
                    if (layout.compactLandscape) {
                        Modifier
                            .fillMaxSize()
                            .safeDrawingPadding()
                            .verticalScroll(rememberScrollState())
                    } else {
                        Modifier
                    }
                )
                .padding(
                    top = when {
                        layout.compactLandscape -> CompactLandscapePadding
                        layout.centerContent -> 0.dp
                        else -> WindowInsets.statusBars.asPaddingValues()
                            .calculateTopPadding() + layout.titleAuthorGap * 1.5f
                    },
                    bottom = if (layout.compactLandscape) CompactLandscapePadding else 0.dp
                )
        ) {
            CurtainTitle(layout)
            Text(text = stringResource(R.string.curtain_author), style = layout.authorStyle)
        }
    }
}

@Composable
private fun CurtainTitle(layout: CurtainLayout) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TitleLine(R.string.curtain_title_the, layout.cursiveTitleStyle)
        TitleLine(R.string.curtain_title_wonderful, layout.titleStyle)
        TitleLine(R.string.curtain_title_names, layout.titleStyle)
        Row(
            horizontalArrangement = Arrangement.spacedBy(layout.ofOurGap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleLine(R.string.curtain_title_of, layout.cursiveTitleStyle)
            TitleLine(R.string.curtain_title_our, layout.titleStyle)
        }
        TitleLine(R.string.curtain_title_wonderful, layout.titleStyle)
        TitleLine(R.string.curtain_title_lord, layout.titleStyle)
    }
}

@Composable
private fun TitleLine(textRes: Int, style: TextStyle) {
    Text(text = stringResource(textRes), style = style)
}

@Preview(name = "Phone", widthDp = 390, heightDp = 844)
@Preview(name = "Tablet", widthDp = 800, heightDp = 1280)
@Preview(name = "Small tablet landscape", widthDp = 960, heightDp = 600)
@Preview(name = "Phone landscape", widthDp = 844, heightDp = 390)
@Preview(name = "Large window", widthDp = 1200, heightDp = 1000)
@Composable
private fun CurtainScreenPreview() {
    WonderfulNamesTheme { CurtainScreen() }
}
