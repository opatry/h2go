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

package net.opatry.h2go.preference.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.opatry.h2go.preference.data.entity.UserPreferencesEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesDaoTest {
    private lateinit var db: UserPreferencesTestDatabase
    private lateinit var dao: UserPreferencesDao

    private val dummyPreferences by lazy {
        UserPreferencesEntity(
            dailyTarget = 42,
            glassVolume = 42,
            volumeUnit = "Milliliter",
            areNotificationsEnabled = false,
            notificationFrequencyInHours = 0
        )
    }

    @BeforeEach
    fun setup() {
        db = Room.inMemoryDatabaseBuilder<UserPreferencesTestDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()
        dao = db.userPreferencesDao()
    }

    @AfterEach
    fun teardown() = db.close()

    @Test
    fun `given empty data, when getting user preferences, then returns null`() = runTest {
        // Given

        // When
        val resultingPreferences = dao.getUserPreferences().first()

        // Then
        assertThat(resultingPreferences).isNull()
    }

    companion object {
        @JvmStatic
        fun preferencesParams() = arrayOf(
            UserPreferencesEntity(
                dailyTarget = 2000,
                glassVolume = 250,
                volumeUnit = "Milliliter",
                areNotificationsEnabled = true,
                notificationFrequencyInHours = 1,
            ),
            UserPreferencesEntity(
                dailyTarget = 1000,
                glassVolume = 50,
                volumeUnit = "Oz",
                areNotificationsEnabled = true,
                notificationFrequencyInHours = 1,
            ),
            UserPreferencesEntity(
                dailyTarget = 1000,
                glassVolume = 50,
                volumeUnit = "Oz",
                areNotificationsEnabled = false,
                notificationFrequencyInHours = 1,
            ),
            UserPreferencesEntity(
                dailyTarget = 0,
                glassVolume = 0,
                volumeUnit = "Oz",
                areNotificationsEnabled = false,
                notificationFrequencyInHours = 0,
            ),
        )
    }

    @ParameterizedTest(name = "given {0}, when getting user preferences, then returns them")
    @MethodSource("preferencesParams")
    fun insertPreferences(
        givenPreferences: UserPreferencesEntity
    ) = runTest {
        // Given
        val id = dao.upsert(givenPreferences)

        // When
        val resultingPreferences = dao.getUserPreferences().first()

        // Then
        assertThat(resultingPreferences).isEqualTo(givenPreferences.copy(id = id))
    }

    @ParameterizedTest(name = "given {0}, when upserting with new values, then returns them")
    @MethodSource("preferencesParams")
    fun updatePreferences(
        givenPreferences: UserPreferencesEntity
    ) = runTest {
        // Given
        val initialPreferences = dummyPreferences
        val id = dao.upsert(initialPreferences)

        // When
        val updatedPreferences = givenPreferences.copy(id = id)
        dao.upsert(updatedPreferences)

        // Then
        val resultingPreferences = dao.getUserPreferences().first()
        assertThat(resultingPreferences).isEqualTo(updatedPreferences)
    }

    @Test
    fun `given existing preferences, when clearing, then the data is empty`() = runTest {
        // Given
        dao.upsert(dummyPreferences)

        // When
        dao.clear()

        // Then
        val resultingPreferences = dao.getUserPreferences().first()
        assertThat(resultingPreferences).isNull()
    }

    @Test
    fun `given existing preferences and default, when reseting, then user preferences results to default`() = runTest {
        // Given
        dao.upsert(dummyPreferences)

        // When
        val defaultPreferences = dummyPreferences.copy(dailyTarget = 5000, volumeUnit = "Oz", areNotificationsEnabled = true)
        dao.reset(defaultPreferences)

        // Then
        val resultingPreferences = dao.getUserPreferences().first()
        assertThat(resultingPreferences).isEqualTo(defaultPreferences)
    }
} 