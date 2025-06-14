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

import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.serialization) apply false
    alias(libs.plugins.jetbrains.kotlin.compose.compiler) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.about.libraries) apply false
    alias(libs.plugins.kover)
}

val koverProjects = listOf(
    projects.h2goApp,
    projects.preferences,
    projects.onboarding,
)

dependencies {
    koverProjects.onEach { koverProject ->
        kover(koverProject)
    }
}

val koverExcludedClasses = listOf(
    "net.opatry.h2go.app.data.H2GoDatabase*",
    "net.opatry.h2go.app.data.di.*",
    "net.opatry.h2go.app.navigation.*",
    "net.opatry.h2go.app.ComposableSingletons*",
    "net.opatry.h2go.app.H2GoApplication",
    "net.opatry.h2go.preference.data.*Dao",
    "net.opatry.h2go.preference.data.*Dao\$DefaultImpls",
    "net.opatry.h2go.preference.di.*",
    "net.opatry.h2go.onboarding.di.*",
    "net.opatry.h2go.onboarding.navigation.*",
    "net.opatry.h2go.onboarding.ui.*",
)

kover {
    currentProject {
        createVariant("coverage") {}
    }

    reports {
        filters {
            excludes {
                androidGeneratedClasses()
                classes(*koverExcludedClasses.toTypedArray())
            }
        }

        variant("coverage") {
            verify {
                rule("Instruction coverage") {
                    minBound(
                        minValue = 95,
                        coverageUnits = CoverageUnit.INSTRUCTION
                    )
                }
                rule("Line coverage") {
                    minBound(
                        minValue = 95,
                        coverageUnits = CoverageUnit.LINE
                    )
                }
                rule("Branch coverage") {
                    minBound(
                        minValue = 95,
                        coverageUnits = CoverageUnit.BRANCH
                    )
                }
            }
        }
    }
}

subprojects {
    tasks {
        findByName("test") ?: return@tasks
        named<Test>("test") {
            testLogging {
                events("failed", "passed", "skipped")

                exceptionFormat = TestExceptionFormat.SHORT

                debug {
                    exceptionFormat = TestExceptionFormat.FULL
                }
            }
        }
    }

    if (project.name in koverProjects.map { it.name }) {
        project.afterEvaluate {
            apply(plugin = libs.plugins.kover.get().pluginId)
            kover {
                currentProject {
                    createVariant("coverage") {
                        addWithDependencies("debug", optional = true)
                    }
                }
            }
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
