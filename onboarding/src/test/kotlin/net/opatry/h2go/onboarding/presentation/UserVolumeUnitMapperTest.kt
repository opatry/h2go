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

import androidx.annotation.StringRes
import net.opatry.h2go.onboarding.R
import net.opatry.h2go.preference.domain.VolumeUnit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class UserVolumeUnitMapperTest {

    companion object {
        @JvmStatic
        fun volumeUnitParams() = arrayOf(
            VolumeUnit.Milliliter to UserVolumeUnit.Milliliter,
            VolumeUnit.Oz to UserVolumeUnit.Oz,
        ).map { (domainUnit, presentationUnit) ->
            Arguments.of(domainUnit, presentationUnit)
        }

        @JvmStatic
        fun unitLabelResParams() = arrayOf(
            UserVolumeUnit.Milliliter to R.string.onboarding_preferences_volume_unit_ml,
            UserVolumeUnit.Oz to R.string.onboarding_preferences_volume_unit_oz,
        ).map { (unit, labelRes) ->
            Arguments.of(unit, labelRes)
        }
    }

    private val mapper = UserVolumeUnitMapper()

    @ParameterizedTest(name = "given domain {0}, then should map to presentation {1}")
    @MethodSource("volumeUnitParams")
    fun toPresentation(
        givenDomainUnit: VolumeUnit,
        presentationUnit: UserVolumeUnit,
    ) {
        assertThat(mapper.toPresentation(givenDomainUnit)).isEqualTo(presentationUnit)
    }

    @ParameterizedTest(name = "given presentation {1}, then should map to domain {1}")
    @MethodSource("volumeUnitParams")
    fun toDomain(
        domainUnit: VolumeUnit,
        givenPresentationUnit: UserVolumeUnit,
    ) {
        assertThat(mapper.toDomain(givenPresentationUnit)).isEqualTo(domainUnit)
    }

    @ParameterizedTest(name = "given {1}, then should map to proper label res")
    @MethodSource("unitLabelResParams")
    fun `given Oz, then should return oz string resource as unit`(
        givenUnit: UserVolumeUnit,
        @StringRes expectedLabelRes: Int,
    ) {
        // When
        val unitLabelRes = givenUnit.labelRes

        // Then
        assertThat(unitLabelRes).isEqualTo(expectedLabelRes)
    }
}
