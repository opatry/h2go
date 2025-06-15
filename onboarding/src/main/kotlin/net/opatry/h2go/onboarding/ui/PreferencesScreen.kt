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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.opatry.h2go.onboarding.R
import net.opatry.h2go.onboarding.presentation.PreferencesEvent
import net.opatry.h2go.onboarding.presentation.PreferencesUiState
import net.opatry.h2go.onboarding.presentation.PreferencesViewModel
import net.opatry.h2go.onboarding.presentation.UserVolumeUnit
import net.opatry.h2go.onboarding.ui.PreferencesScreenTestTag.SAVE_BUTTON
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@VisibleForTesting
object PreferencesScreenTestTag {
    const val SAVE_BUTTON = "PREFERENCES_SAVE_BUTTON"
}

@Composable
fun PreferencesScreen(
    onNavigateToMain: () -> Unit,
    viewModel: PreferencesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.eventsFlow.collect { event ->
            when (event) {
                is PreferencesEvent.Error -> error = event.message
                PreferencesEvent.NavigateToMain -> onNavigateToMain()
            }
        }
    }

    error?.let { errorMessage ->
        AlertDialog(
            onDismissRequest = { error = null },
            title = { Text(text = stringResource(R.string.onboarding_error_title)) },
            text = { Text(text = errorMessage) },
            confirmButton = {
                Button(onClick = { error = null }) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
        )
    }

    PreferencesScreen(
        uiState = uiState,
        onVolumeUnitSelected = viewModel::updateVolumeUnit,
        onNotificationsEnabledChanged = viewModel::enableNotifications,
        onNotificationFrequencyChanged = viewModel::updateNotificationsFrequency,
        onSaveClicked = viewModel::savePreferences,
    )
}

@Composable
fun PreferencesScreen(
    uiState: PreferencesUiState,
    onVolumeUnitSelected: (UserVolumeUnit) -> Unit,
    onNotificationsEnabledChanged: (Boolean) -> Unit,
    onNotificationFrequencyChanged: (Duration) -> Unit,
    onSaveClicked: () -> Unit,
) {
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
                listOf(UserVolumeUnit.Milliliter, UserVolumeUnit.Oz).forEach { unit ->
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
                            text = stringResource(unit.labelRes),
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
                    val frequencyInHours = uiState.notificationsFrequency.inWholeHours.toInt()
                    val frequencyBoundsInHours = uiState.notificationsFrequencyBounds.let {
                        it.start.inWholeHours.toFloat()..it.endInclusive.inWholeHours.toFloat()
                    }
                    val frequencySteps = with(uiState.notificationsFrequencyBounds) {
                        endInclusive.inWholeHours - start.inWholeHours - 1
                    }.coerceAtLeast(0).toInt()
                    Slider(
                        value = uiState.notificationsFrequency.inWholeHours.toFloat(),
                        onValueChange = { onNotificationFrequencyChanged(it.toInt().hours) },
                        valueRange = frequencyBoundsInHours,
                        steps = frequencySteps,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    Text(
                        text = pluralStringResource(
                            R.plurals.onboarding_preferences_notification_frequency_value,
                            frequencyInHours,
                            frequencyInHours,
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
            modifier = Modifier
                .fillMaxWidth()
                .testTag(SAVE_BUTTON),
            enabled = !uiState.isSaving,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = stringResource(R.string.onboarding_preferences_save_button))
                AnimatedVisibility(visible = uiState.isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}

private class PreferencesPreviewParameterProvider : PreviewParameterProvider<PreferencesUiState> {
    override val values: Sequence<PreferencesUiState>
        get() = sequenceOf(
            PreferencesUiState(
                selectedVolumeUnit = UserVolumeUnit.Milliliter,
                areNotificationsEnabled = false,
                notificationsFrequency = 1.hours,
                notificationsFrequencyBounds = 1.hours..6.hours,
                isSaving = false,
            ),
            PreferencesUiState(
                selectedVolumeUnit = UserVolumeUnit.Oz,
                areNotificationsEnabled = true,
                notificationsFrequency = 7.hours,
                notificationsFrequencyBounds = 1.hours..12.hours,
                isSaving = false,
            ),
            PreferencesUiState(
                selectedVolumeUnit = UserVolumeUnit.Oz,
                areNotificationsEnabled = true,
                notificationsFrequency = 2.hours,
                notificationsFrequencyBounds = 1.hours..6.hours,
                isSaving = true,
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
            )
        }
    }
} 