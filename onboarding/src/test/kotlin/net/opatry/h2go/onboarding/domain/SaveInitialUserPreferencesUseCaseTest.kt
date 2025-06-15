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

package net.opatry.h2go.onboarding.domain

import kotlinx.coroutines.test.runTest
import net.opatry.h2go.preference.domain.UserPreferences
import net.opatry.h2go.preference.domain.UserPreferencesRepository
import net.opatry.h2go.preference.domain.VolumeUnit
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@ExtendWith(MockitoExtension::class)
class SaveInitialUserPreferencesUseCaseTest {

    data class PreferencesParam(
        val dailyTarget: Int,
        val glassVolume: Int,
        val volumeUnit: VolumeUnit,
        val areNotificationsEnabled: Boolean,
        val notificationsFrequency: Duration,
    )

    companion object {
        @JvmStatic
        fun preferenceParams() = arrayOf(
            PreferencesParam(
                dailyTarget = 2000,
                glassVolume = 250,
                volumeUnit = VolumeUnit.Milliliter,
                areNotificationsEnabled = true,
                notificationsFrequency = 2.hours,
            ),
            PreferencesParam(
                dailyTarget = 64,
                glassVolume = 8,
                volumeUnit = VolumeUnit.Oz,
                areNotificationsEnabled = false,
                notificationsFrequency = 4.hours,
            ),
        )
    }

    @Mock
    private lateinit var repository: UserPreferencesRepository

    @InjectMocks
    private lateinit var useCase: SaveInitialUserPreferencesUseCase

    @ParameterizedTest(name = "given {0}, when saving user preferences, then should update repository with given data")
    @MethodSource("preferenceParams")
    fun saveUserPreferences(
        // Given
        givenParam: PreferencesParam,
    ) = runTest {

        // When
        useCase(
            volumeUnit = givenParam.volumeUnit,
            areNotificationsEnabled = givenParam.areNotificationsEnabled,
            notificationsFrequency = givenParam.notificationsFrequency,
        )

        // Then
        verify(repository).updateUserPreferences(
            UserPreferences(
                dailyTarget = givenParam.dailyTarget,
                glassVolume = givenParam.glassVolume,
                volumeUnit = givenParam.volumeUnit,
                areNotificationsEnabled = givenParam.areNotificationsEnabled,
                notificationsFrequency = givenParam.notificationsFrequency,
            )
        )
    }
} 