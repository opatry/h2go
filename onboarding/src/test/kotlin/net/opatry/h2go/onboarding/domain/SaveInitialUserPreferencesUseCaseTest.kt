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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class SaveInitialUserPreferencesUseCaseTest {

    @Mock
    private lateinit var repository: UserPreferencesRepository

    @InjectMocks
    private lateinit var useCase: SaveInitialUserPreferencesUseCase

    @Test
    fun `when saving preferences with ml unit, saves correct values`() = runTest {
        // Given
        val givenVolumeUnit = VolumeUnit.Milliliter
        val givenNotificationsEnabled = true
        val givenNotificationFrequency = 2

        // When
        useCase(
            volumeUnit = givenVolumeUnit,
            areNotificationsEnabled = givenNotificationsEnabled,
            notificationFrequencyInHours = givenNotificationFrequency,
        )

        // Then
        verify(repository).updateUserPreferences(
            UserPreferences(
                dailyTarget = 2000,
                glassVolume = 250,
                volumeUnit = givenVolumeUnit,
                areNotificationsEnabled = givenNotificationsEnabled,
                notificationFrequencyInHours = givenNotificationFrequency,
            )
        )
    }

    // FIXME parameterized test
    @Test
    fun `when saving preferences with oz unit, saves correct values`() = runTest {
        // Given
        val givenVolumeUnit = VolumeUnit.Oz
        val givenNotificationsEnabled = true
        val givenNotificationFrequency = 2

        // When
        useCase(
            volumeUnit = givenVolumeUnit,
            areNotificationsEnabled = givenNotificationsEnabled,
            notificationFrequencyInHours = givenNotificationFrequency,
        )

        // Then
        verify(repository).updateUserPreferences(
            UserPreferences(
                dailyTarget = 64,
                glassVolume = 8,
                volumeUnit = givenVolumeUnit,
                areNotificationsEnabled = givenNotificationsEnabled,
                notificationFrequencyInHours = givenNotificationFrequency,
            )
        )
    }
} 