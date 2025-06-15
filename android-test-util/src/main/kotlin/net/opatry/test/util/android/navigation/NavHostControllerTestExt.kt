package net.opatry.test.util.android.navigation

import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import org.assertj.core.api.Assertions.assertThat


inline fun <reified T : Any> NavHostController.assertRoute() {
    assertThat(currentDestination?.hasRoute<T>())
        .withFailMessage("Expected %s route but current route is %s", T::class.java.name, currentDestination?.route)
        .isTrue()
}