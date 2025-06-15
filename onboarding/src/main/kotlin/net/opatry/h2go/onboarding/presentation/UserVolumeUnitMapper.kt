package net.opatry.h2go.onboarding.presentation

import net.opatry.h2go.preference.domain.VolumeUnit

class UserVolumeUnitMapper {
    fun toPresentation(unit: VolumeUnit) = when(unit) {
        VolumeUnit.Milliliter -> UserVolumeUnit.Milliliter
        VolumeUnit.Oz -> UserVolumeUnit.Oz
    }

    fun toDomain(unit: UserVolumeUnit) = when(unit) {
        UserVolumeUnit.Milliliter -> VolumeUnit.Milliliter
        UserVolumeUnit.Oz -> VolumeUnit.Oz
    }
}