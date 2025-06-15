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
