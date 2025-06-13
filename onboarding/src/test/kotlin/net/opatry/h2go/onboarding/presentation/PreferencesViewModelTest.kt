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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.opatry.h2go.onboarding.domain.SaveInitialUserPreferencesUseCase
import net.opatry.h2go.preference.domain.VolumeUnit
import net.opatry.test.util.MainDispatcherExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class, MainDispatcherExtension::class)
class PreferencesViewModelTest {

    @Mock
    private lateinit var useCase: SaveInitialUserPreferencesUseCase

    @InjectMocks
    private lateinit var viewModel: PreferencesViewModel

    @Test
    fun `when volume unit selected, updates state`() = runTest {
        // Given

        // When
        assertThat(viewModel.uiState.value.selectedVolumeUnit).isEqualTo(VolumeUnit.Milliliter)
        viewModel.onVolumeUnitSelected(VolumeUnit.Oz)

        // Then
        assertThat(viewModel.uiState.value.selectedVolumeUnit).isEqualTo(VolumeUnit.Oz)
    }

    @Test
    fun `when notifications enabled changed, updates state`() = runTest {
        // Given

        // When
        assertThat(viewModel.uiState.value.areNotificationsEnabled).isTrue()
        viewModel.onNotificationsEnabledChanged(false)

        // Then
        assertThat(viewModel.uiState.value.areNotificationsEnabled).isFalse()
    }

    @Test
    fun `when notification frequency changed, updates state`() = runTest {
        // Given

        // When
        assertThat(viewModel.uiState.value.notificationFrequencyInHours).isEqualTo(2)
        viewModel.onNotificationFrequencyChanged(4)

        // Then
        assertThat(viewModel.uiState.value.notificationFrequencyInHours).isEqualTo(4)
    }

    @Test
    fun `when save clicked, saves preferences and navigates to main`() = runTest {
        // Given
        val collectedStates = mutableListOf<PreferencesUiState>()
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.toList(collectedStates)
        }

        // When
        viewModel.onSaveClicked()

        // Then
        val defaultState = viewModel.uiState.value
        advanceUntilIdle()
        collectorJob.cancel()
        assertThat(collectedStates).containsExactly(
            defaultState,
            defaultState.copy(isSaving = true),
            defaultState.copy(isSaving = false, shouldNavigateToMain = true),
        )

        verify(useCase).invoke(
            volumeUnit = VolumeUnit.Milliliter,
            areNotificationsEnabled = true,
            notificationFrequencyInHours = 2,
        )
    }

    @Test
    fun `when save fails, shows error`() = runTest {
        // Given
        val error = "error message"
        given(
            useCase.invoke(
                volumeUnit = VolumeUnit.Milliliter,
                areNotificationsEnabled = true,
                notificationFrequencyInHours = 2,
            )
        ).willThrow(RuntimeException(error))

        val collectedStates = mutableListOf<PreferencesUiState>()
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.toList(collectedStates)
        }

        // When
        viewModel.onSaveClicked()

        // Then
        val defaultState = viewModel.uiState.value
        advanceUntilIdle()
        collectorJob.cancel()
        assertThat(collectedStates).containsExactly(
            defaultState,
            defaultState.copy(isSaving = true),
            defaultState.copy(isSaving = false, error = error),
        )
    }

    @Test
    fun `when error shown, clears error`() = runTest {
        // Given
        given(
            useCase.invoke(
                volumeUnit = VolumeUnit.Milliliter,
                areNotificationsEnabled = true,
                notificationFrequencyInHours = 2,
            )
        ).willThrow(RuntimeException("error"))

        // When
        viewModel.onSaveClicked()
        advanceUntilIdle()
        viewModel.onErrorShown()

        // Then
        assertThat(viewModel.uiState.value.error).isNull()
    }
}