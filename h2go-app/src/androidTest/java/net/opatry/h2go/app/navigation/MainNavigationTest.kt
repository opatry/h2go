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

package net.opatry.h2go.app.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityCompat.recreate
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import net.opatry.h2go.di.databaseTestModule
import net.opatry.h2go.onboarding.di.onboardingModule
import net.opatry.h2go.onboarding.navigation.OnboardingRoutes
import net.opatry.h2go.onboarding.ui.PreferencesScreenTestTag.SAVE_BUTTON
import net.opatry.h2go.onboarding.ui.WelcomeScreenTestTag.CONTINUE_BUTTON
import net.opatry.h2go.preference.di.preferencesModule
import net.opatry.test.util.android.KoinTestRule
import net.opatry.test.util.android.navigation.assertRoute
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class MainNavigationTest {
    @get: Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val koinTestRule = KoinTestRule(
        listOf(
            databaseTestModule,
            preferencesModule,
            onboardingModule,
        )
    )

    private lateinit var navController: TestNavHostController

    @Before
    fun setupNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            MainNavigation(navController)
        }
    }

    @Test
    fun givenMainNavigation_shouldLandToWelcomeRoute() {
        // Then
        navController.assertRoute<OnboardingRoutes.Welcome>()
    }

    @Test
    fun givenMainNavigation_whenClickingWelcomeButton_shouldNavigateToPreferencesRoute() {
        // When
        composeTestRule.onNodeWithTag(CONTINUE_BUTTON)
            .performClick()
        composeTestRule.waitForIdle()

        // Then
        navController.assertRoute<OnboardingRoutes.Preferences>()
    }

    @Test
    fun givenPreferencesScreen_whenClickingSaveButton_shouldNavigateToMainRoute() {
        // Given
        composeTestRule.onNodeWithTag(CONTINUE_BUTTON)
            .performClick()
        composeTestRule.waitUntilExactlyOneExists(hasTestTag(SAVE_BUTTON))

        // When
        composeTestRule.onNodeWithTag(SAVE_BUTTON)
            .performClick()
        composeTestRule.waitForIdle()

        // Then
        navController.assertRoute<MainRoutes.Main>()
    }

    @Test
    fun givenAchievedOnboarding_shouldLandToMainRoute() {
        // Given
        composeTestRule.onNodeWithTag(CONTINUE_BUTTON)
            .performClick()
        composeTestRule.waitUntilExactlyOneExists(hasTestTag(SAVE_BUTTON))

        composeTestRule.onNodeWithTag(SAVE_BUTTON)
            .performClick()
        composeTestRule.waitForIdle()

        // When
        composeTestRule.activity.runOnUiThread {
            // FIXME not really stressing app restart scenario
            recreate(composeTestRule.activity)
        }

        // Then
        navController.assertRoute<MainRoutes.Main>()
    }
}
