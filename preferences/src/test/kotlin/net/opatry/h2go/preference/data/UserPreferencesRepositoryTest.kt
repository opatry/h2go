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
import net.opatry.h2go.preference.domain.UserPreferences
import net.opatry.h2go.preference.domain.UserPreferencesRepository
import net.opatry.h2go.preference.domain.VolumeUnit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesRepositoryTest {
    private val mapper: UserPreferencesMapper = UserPreferencesMapper()
    private lateinit var db: UserPreferencesTestDatabase
    private lateinit var dao: UserPreferencesDao
    private lateinit var repository: UserPreferencesRepository

    @BeforeEach
    fun setup() {
        db = Room.inMemoryDatabaseBuilder<UserPreferencesTestDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()
        dao = db.userPreferencesDao()
        repository = UserPreferencesRepositoryImpl(
            dao = dao,
            mapper = mapper
        )
    }

    @AfterEach
    fun teardown() = db.close()

    @Test
    fun `given empty data, when getting user preferences, then returns null`() = runTest {
        // Given

        // When
        val prefs = repository.getUserPreferences().first()

        // Then
        assertThat(prefs).isNull()
    }

    @Test
    fun `given invalid data, when getting user preferences, then returns null`() = runTest {
        // Given
        dao.upsert(
            UserPreferencesEntity(
                dailyTarget = 100,
                glassVolume = 10,
                volumeUnit = "invalid unit",
                areNotificationsEnabled = false,
                notificationFrequencyInHours = 1,
            )
        )

        // When
        val prefs = repository.getUserPreferences().first()

        // Then
        assertThat(prefs).isNull()
    }

    @Test
    fun `given existing preferences, when updating user preferences, then returns updated`() = runTest {
        // Given
        dao.upsert(
            UserPreferencesEntity(
                dailyTarget = 100,
                glassVolume = 10,
                volumeUnit = "Milliliter",
                areNotificationsEnabled = false,
                notificationFrequencyInHours = 1,
            )
        )

        // When
        val updatedPreferences = UserPreferences(1800, 200, VolumeUnit.Oz, false, 4.hours)
        repository.updateUserPreferences(updatedPreferences)

        // Then
        val resultingPreferences = repository.getUserPreferences().first()
        assertThat(resultingPreferences).isEqualTo(updatedPreferences)
    }

    @Test
    fun `given existing preferences, when resetting, then returns default preferences`() = runTest {
        // Given
        dao.upsert(
            UserPreferencesEntity(
                dailyTarget = 100,
                glassVolume = 10,
                volumeUnit = "Oz",
                areNotificationsEnabled = false,
                notificationFrequencyInHours = 1,
            )
        )

        // When
        val defaultPreferences = UserPreferences(2000, 25, VolumeUnit.Milliliter, true, 2.hours)
        repository.resetUserPreferences(defaultPreferences)

        // Then
        val resultingPreferences = repository.getUserPreferences().first()
        assertThat(resultingPreferences).isEqualTo(defaultPreferences)
    }
} 