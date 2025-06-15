/*
 * Copyright (c) 2025 Olivier Patry
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.opatry.h2go.onboarding.ui

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.opatry.h2go.onboarding.R
import net.opatry.h2go.onboarding.presentation.WelcomeUiState
import net.opatry.h2go.onboarding.presentation.WelcomeViewModel
import net.opatry.h2go.onboarding.ui.WelcomeScreenTestTag.CONTINUE_BUTTON
import org.koin.androidx.compose.koinViewModel

@VisibleForTesting
object WelcomeScreenTestTag {
    const val CONTINUE_BUTTON = "WELCOME_CONTINUE_BUTTON"
}

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = koinViewModel(),
    onContinueClicked: () -> Unit,
    onNavigateToMain: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        WelcomeUiState.Idle -> LaunchedEffect(uiState) {
            viewModel.checkUserPreferences()
        }

        WelcomeUiState.NavigateToMain -> onNavigateToMain()
        WelcomeUiState.ShowWelcome -> WelcomeScreen(onContinueClicked = onContinueClicked)
    }
}

@Composable
internal fun WelcomeScreen(
    onContinueClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = stringResource(R.string.onboarding_welcome_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.onboarding_welcome_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onContinueClicked,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(CONTINUE_BUTTON),
        ) {
            Text(text = stringResource(R.string.onboarding_welcome_continue_button))
        }
    }
}

@PreviewLightDark
@Composable
private fun WelcomeScreenContentPreview() {
    MaterialTheme {
        Surface {
            WelcomeScreen(
                onContinueClicked = {},
            )
        }
    }
}
