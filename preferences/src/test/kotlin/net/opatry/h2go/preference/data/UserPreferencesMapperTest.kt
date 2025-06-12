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

import net.opatry.h2go.preference.data.entity.UserPreferencesEntity
import net.opatry.h2go.preference.domain.UserPreferences
import net.opatry.h2go.preference.domain.VolumeUnit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class UserPreferencesMapperTest {

    private val mapper = UserPreferencesMapper()

    companion object {
        @JvmStatic
        fun preferencesParams() = arrayOf(
            Triple(VolumeUnit.Milliliter, "Milliliter", true),
            Triple(VolumeUnit.Milliliter, "Milliliter", false),
            Triple(VolumeUnit.Oz, "Oz", true),
            Triple(VolumeUnit.Oz, "Oz", false),
        ).map { (unit, unitStr, areNotificationsEnabled) ->
            Arguments.of(unit, unitStr, areNotificationsEnabled)
        }
    }

    @ParameterizedTest(name = "given volumeUnit={0} and areNotificationsEnabled={2}, then should map correctly")
    @MethodSource("preferencesParams")
    fun toDomain(
        volumeUnit: VolumeUnit,
        volumeUnitStr: String,
        areNotificationsEnabled: Boolean
    ) {
        val entity = UserPreferencesEntity(
            dailyTarget = 2000,
            glassVolume = 250,
            volumeUnit = volumeUnitStr,
            areNotificationsEnabled = areNotificationsEnabled,
            notificationFrequencyInHours = 2
        )

        val result = mapper.toDomain(entity)

        assertThat(result.dailyTarget).isEqualTo(2000)
        assertThat(result.glassVolume).isEqualTo(250)
        assertThat(result.volumeUnit).isEqualTo(volumeUnit)
        assertThat(result.areNotificationsEnabled).isEqualTo(areNotificationsEnabled)
        assertThat(result.notificationFrequencyInHours).isEqualTo(2)
    }

    @ParameterizedTest(name = "given volumeUnit=${0} and areNotificationsEnabled=${1}, then should map correctly")
    @MethodSource("preferencesParams")
    fun `toEntity should map correctly`(
        volumeUnit: VolumeUnit,
        volumeUnitStr: String,
        areNotificationsEnabled: Boolean
    ) {
        val preferences = UserPreferences(
            dailyTarget = 1500,
            glassVolume = 300,
            volumeUnit = volumeUnit,
            areNotificationsEnabled = areNotificationsEnabled,
            notificationFrequencyInHours = 3,
        )

        val result = mapper.toEntity(preferences)

        assertThat(result.dailyTarget).isEqualTo(1500)
        assertThat(result.glassVolume).isEqualTo(300)
        assertThat(result.volumeUnit).isEqualTo(volumeUnitStr)
        assertThat(result.areNotificationsEnabled).isEqualTo(areNotificationsEnabled)
        assertThat(result.notificationFrequencyInHours).isEqualTo(3)
    }
}