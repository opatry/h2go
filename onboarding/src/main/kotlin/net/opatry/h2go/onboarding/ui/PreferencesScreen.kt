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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.opatry.h2go.onboarding.R
import net.opatry.h2go.onboarding.presentation.PreferencesUiState
import net.opatry.h2go.onboarding.presentation.PreferencesViewModel
import net.opatry.h2go.preference.domain.VolumeUnit
import org.koin.androidx.compose.koinViewModel

@Composable
fun PreferencesScreen(
    onNavigateToMain: () -> Unit,
    viewModel: PreferencesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PreferencesScreen(
        uiState = uiState,
        onVolumeUnitSelected = viewModel::onVolumeUnitSelected,
        onNotificationsEnabledChanged = viewModel::onNotificationsEnabledChanged,
        onNotificationFrequencyChanged = viewModel::onNotificationFrequencyChanged,
        onSaveClicked = viewModel::onSaveClicked,
        onErrorShown = viewModel::onErrorShown,
        onNavigateToMain = onNavigateToMain,
    )
}

@Composable
fun PreferencesScreen(
    uiState: PreferencesUiState,
    onVolumeUnitSelected: (VolumeUnit) -> Unit,
    onNotificationsEnabledChanged: (Boolean) -> Unit,
    onNotificationFrequencyChanged: (Int) -> Unit,
    onSaveClicked: () -> Unit,
    onErrorShown: () -> Unit,
    onNavigateToMain: () -> Unit,
) {
    if (uiState.shouldNavigateToMain) {
        onNavigateToMain()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.onboarding_preferences_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.onboarding_preferences_volume_unit_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                VolumeUnit.entries.forEach { unit ->
                    val isSelected = unit == uiState.selectedVolumeUnit
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .selectable(
                                selected = isSelected,
                                onClick = { onVolumeUnitSelected(unit) },
                                role = Role.RadioButton,
                            ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                        )
                        Text(
                            text = stringResource(
                                when (unit) {
                                    VolumeUnit.Milliliter -> R.string.onboarding_preferences_volume_unit_ml
                                    VolumeUnit.Oz -> R.string.onboarding_preferences_volume_unit_oz
                                }
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.onboarding_preferences_notifications_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.onboarding_preferences_notifications_enable),
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = uiState.areNotificationsEnabled,
                    onCheckedChange = { onNotificationsEnabledChanged(it) },
                )
            }
            AnimatedVisibility(visible = uiState.areNotificationsEnabled) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.onboarding_preferences_notification_frequency_title),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    // TODO min/max bounds and step from ViewModel
                    Slider(
                        value = uiState.notificationFrequencyInHours.toFloat(),
                        onValueChange = { onNotificationFrequencyChanged(it.toInt()) },
                        valueRange = 1f..6f,
                        steps = 4,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    // TODO quantity string plural
                    Text(
                        text = pluralStringResource(
                            R.plurals.onboarding_preferences_notification_frequency_value,
                            uiState.notificationFrequencyInHours,
                            uiState.notificationFrequencyInHours,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSaveClicked,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving,
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(end = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Text(text = stringResource(R.string.onboarding_preferences_save_button))
        }
    }

    uiState.error?.let { error ->
        // FIXME shared flow
        AlertDialog(
            onDismissRequest = onErrorShown,
            title = { Text(text = stringResource(R.string.onboarding_error_title)) },
            text = { Text(text = error) },
            confirmButton = {
                Button(onClick = onErrorShown) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
        )
    }
}

private class PreferencesPreviewParameterProvider : PreviewParameterProvider<PreferencesUiState> {
    override val values: Sequence<PreferencesUiState>
        get() = sequenceOf(
            PreferencesUiState(
                areNotificationsEnabled = false,
                notificationFrequencyInHours = 1,
                isSaving = false,
                shouldNavigateToMain = false,
                selectedVolumeUnit = VolumeUnit.Milliliter,
                error = null,
            ),
            PreferencesUiState(
                areNotificationsEnabled = true,
                notificationFrequencyInHours = 2,
                isSaving = false,
                shouldNavigateToMain = false,
                selectedVolumeUnit = VolumeUnit.Oz,
                error = null,
            ),
            PreferencesUiState(
                areNotificationsEnabled = true,
                notificationFrequencyInHours = 2,
                isSaving = true,
                shouldNavigateToMain = false,
                selectedVolumeUnit = VolumeUnit.Oz,
                error = null,
            ),
            PreferencesUiState(
                areNotificationsEnabled = false,
                notificationFrequencyInHours = 2,
                isSaving = true,
                shouldNavigateToMain = false,
                selectedVolumeUnit = VolumeUnit.Oz,
                error = "Shit happens",
            ),
        )
}

@PreviewLightDark
@Composable
private fun PreferencesScreenPreview(
    @PreviewParameter(PreferencesPreviewParameterProvider::class)
    uiState: PreferencesUiState
) {
    MaterialTheme {
        Surface {
            PreferencesScreen(
                uiState = uiState,
                onVolumeUnitSelected = {},
                onNotificationsEnabledChanged = {},
                onNotificationFrequencyChanged = {},
                onSaveClicked = {},
                onErrorShown = {},
                onNavigateToMain = {},
            )
        }
    }
} 