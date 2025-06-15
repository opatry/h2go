package net.opatry.h2go.onboarding.presentation

import androidx.annotation.StringRes
import net.opatry.h2go.onboarding.R

// TODO change as "value+unit" and add Glass unit
//  then convert to ml or oz for domain layer
//  would let users choose a value with meaning for them
sealed interface UserVolumeUnit {
    @get:StringRes
    val labelRes: Int

    data object Milliliter : UserVolumeUnit {
        override val labelRes: Int
            get() = R.string.onboarding_preferences_volume_unit_ml
    }

    data object Oz : UserVolumeUnit {
        override val labelRes: Int
            get() = R.string.onboarding_preferences_volume_unit_oz
    }
}