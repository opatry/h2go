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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class, MainDispatcherExtension::class)
class PreferencesViewModelTest {

    companion object {
        @JvmStatic
        fun saveErrorTestCases() = arrayOf(
            RuntimeException() to "Unknown error",
            IllegalArgumentException("Some error") to "Some error",
        ).map { (givenException, expectedMessage) ->
            Arguments.of(givenException, expectedMessage)
        }
    }

    @Mock
    private lateinit var useCase: SaveInitialUserPreferencesUseCase

    @Mock
    private lateinit var mapper: UserVolumeUnitMapper

    @InjectMocks
    private lateinit var viewModel: PreferencesViewModel

    @BeforeEach
    fun stubVolumeUnitMapper() {
        lenient().`when`(mapper.toDomain(UserVolumeUnit.Milliliter)).thenReturn(VolumeUnit.Milliliter)
        lenient().`when`(mapper.toDomain(UserVolumeUnit.Oz)).thenReturn(VolumeUnit.Oz)
    }

    @Test
    fun `given default state, when updating volume unit, then should update state accordingly`() = runTest {
        // Given

        // When
        assertThat(viewModel.uiState.value.selectedVolumeUnit).isEqualTo(UserVolumeUnit.Milliliter)
        viewModel.updateVolumeUnit(UserVolumeUnit.Oz)

        // Then
        assertThat(viewModel.uiState.value.selectedVolumeUnit).isEqualTo(UserVolumeUnit.Oz)
    }

    @Test
    fun `given default state, when enabling notifications, then should update state accordingly`() = runTest {
        // Given

        // When
        assertThat(viewModel.uiState.value.areNotificationsEnabled).isTrue()
        viewModel.enableNotifications(false)

        // Then
        assertThat(viewModel.uiState.value.areNotificationsEnabled).isFalse()
    }

    @Test
    fun `given default state, when updating notification frequency, then should update state accordingly`() = runTest {
        // Given

        // When
        assertThat(viewModel.uiState.value.notificationsFrequency).isEqualTo(2.hours)
        viewModel.updateNotificationsFrequency(4.hours)

        // Then
        assertThat(viewModel.uiState.value.notificationsFrequency).isEqualTo(4.hours)
    }

    @Test
    fun `given default state, when saving preferences, then should trigger use case and navigates to main`() = runTest {
        // Given
        val collectedStates = mutableListOf<PreferencesUiState>()
        val stateCollectorJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.toList(collectedStates)
        }
        val collectedEvents = mutableListOf<PreferencesEvent>()
        val eventCollectorJob = launch {
            viewModel.eventsFlow.toList(collectedEvents)
        }

        // When
        viewModel.savePreferences()

        // Then
        val defaultState = viewModel.uiState.value
        advanceUntilIdle()
        stateCollectorJob.cancel()
        eventCollectorJob.cancel()

        assertThat(collectedStates).containsExactly(
            defaultState,
            defaultState.copy(isSaving = true),
            defaultState.copy(isSaving = false),
        )
        assertThat(collectedEvents).containsExactly(PreferencesEvent.NavigateToMain)

        verify(useCase).invoke(
            volumeUnit = VolumeUnit.Milliliter,
            areNotificationsEnabled = true,
            notificationsFrequency = 2.hours,
        )
    }

    @ParameterizedTest(name = "with {0} message, then should show {1}")
    @MethodSource("saveErrorTestCases")
    fun `given default state, when save preferences fails, then should notify error`(
        givenException: Exception,
        expectedErrorMessage: String,
    ) = runTest {
        // Given
        given(
            useCase.invoke(
                volumeUnit = VolumeUnit.Milliliter,
                areNotificationsEnabled = true,
                notificationsFrequency = 2.hours,
            )
        ).willThrow(givenException)

        val collectedStates = mutableListOf<PreferencesUiState>()
        val stateCollectorJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.toList(collectedStates)
        }
        val collectedEvents = mutableListOf<PreferencesEvent>()
        val eventCollectorJob = launch {
            viewModel.eventsFlow.toList(collectedEvents)
        }

        // When
        viewModel.savePreferences()

        // Then
        val defaultState = viewModel.uiState.value
        advanceUntilIdle()
        stateCollectorJob.cancel()
        eventCollectorJob.cancel()

        assertThat(collectedStates).containsExactly(
            defaultState,
            defaultState.copy(isSaving = true),
            defaultState.copy(isSaving = false),
        )
        assertThat(collectedEvents).containsExactly(PreferencesEvent.Error(expectedErrorMessage))
    }
}