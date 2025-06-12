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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import net.opatry.h2go.preference.domain.UserPreferences
import net.opatry.h2go.preference.domain.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val dao: UserPreferencesDao,
    private val mapper: UserPreferencesMapper,
) : UserPreferencesRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getUserPreferences(): Flow<UserPreferences?> = dao.getUserPreferences().mapLatest { stored ->
        runCatching {
            stored?.let(mapper::toDomain)
        }.getOrNull()
    }

    override suspend fun updateUserPreferences(preferences: UserPreferences) {
        dao.upsert(mapper.toEntity(preferences))
    }

    override suspend fun resetUserPreferences(defaultValue: UserPreferences) {
        dao.reset(mapper.toEntity(defaultValue))
    }
}