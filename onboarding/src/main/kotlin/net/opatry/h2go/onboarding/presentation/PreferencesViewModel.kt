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

package net.opatry.h2go.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.opatry.h2go.onboarding.domain.SaveInitialUserPreferencesUseCase
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class PreferencesViewModel(
    private val saveInitialUserPreferencesUseCase: SaveInitialUserPreferencesUseCase,
    private val volumeMapper: UserVolumeUnitMapper,
) : ViewModel() {

    // TODO get default preferences from domain
    private val _uiState = MutableStateFlow(
        PreferencesUiState(
            // FIXME depends on Locale
            // FIXME presentation layer version?
            selectedVolumeUnit = UserVolumeUnit.Milliliter,
            areNotificationsEnabled = true,
            notificationsFrequency = 2.hours,
            notificationsFrequencyBounds = 1.hours..6.hours,
            isSaving = false,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _eventsFlow = MutableSharedFlow<PreferencesEvent>()
    val eventsFlow = _eventsFlow.asSharedFlow()

    fun updateVolumeUnit(unit: UserVolumeUnit) {
        _uiState.update { it.copy(selectedVolumeUnit = unit) }
    }

    fun enableNotifications(enabled: Boolean) {
        _uiState.update { it.copy(areNotificationsEnabled = enabled) }
    }

    fun updateNotificationsFrequency(frequency: Duration) {
        _uiState.update { it.copy(notificationsFrequency = frequency) }
    }

    fun savePreferences() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            runCatching {
                saveInitialUserPreferencesUseCase(
                    volumeUnit = volumeMapper.toDomain(uiState.value.selectedVolumeUnit),
                    areNotificationsEnabled = uiState.value.areNotificationsEnabled,
                    notificationsFrequency = uiState.value.notificationsFrequency,
                )
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false) }
                _eventsFlow.emit(PreferencesEvent.NavigateToMain)
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false) }
                _eventsFlow.emit(PreferencesEvent.Error(e.message ?: "Unknown error"))
            }
        }
    }
}
