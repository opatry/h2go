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
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.opatry.h2go.onboarding.domain.CheckUserPreferencesExistUseCase
import net.opatry.test.util.MainDispatcherExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given


@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class, MainDispatcherExtension::class)
class WelcomeViewModelTest {

    @Mock
    private lateinit var useCase: CheckUserPreferencesExistUseCase

    @InjectMocks
    private lateinit var viewModel: WelcomeViewModel

    @Test
    fun `given already defined preferences, when checking user preferences, then should navigate to main`() = runTest {
        // Given
        given(useCase.invoke()).willReturn(true)

        // When
        viewModel.checkUserPreferences()

        // Then
        assertThat(viewModel.uiState.value).isEqualTo(WelcomeUiState.Idle)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value).isEqualTo(WelcomeUiState.NavigateToMain)
    }

    @Test
    fun `given no defined preferences, when checking user preferences, then should show welcome`() = runTest {
        // Given
        given(useCase.invoke()).willReturn(false)

        // When
        viewModel.checkUserPreferences()

        // Then
        assertThat(viewModel.uiState.value).isEqualTo(WelcomeUiState.Idle)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value).isEqualTo(WelcomeUiState.ShowWelcome)
    }
}