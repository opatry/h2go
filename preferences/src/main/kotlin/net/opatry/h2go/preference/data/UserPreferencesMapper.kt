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
import kotlin.time.Duration.Companion.hours

class UserPreferencesMapper {

    fun toDomain(entity: UserPreferencesEntity) = UserPreferences(
        dailyTarget = entity.dailyTarget,
        glassVolume = entity.glassVolume,
        volumeUnit = VolumeUnit.valueOf(entity.volumeUnit),
        areNotificationsEnabled = entity.areNotificationsEnabled,
        notificationsFrequency = entity.notificationFrequencyInHours.hours,
    )

    fun toEntity(preferences: UserPreferences) = UserPreferencesEntity(
        dailyTarget = preferences.dailyTarget,
        glassVolume = preferences.glassVolume,
        volumeUnit = preferences.volumeUnit.name,
        areNotificationsEnabled = preferences.areNotificationsEnabled,
        notificationFrequencyInHours = preferences.notificationsFrequency.inWholeHours.toInt(),
    )
}