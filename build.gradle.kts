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
)

dependencies {
    koverProjects.onEach { koverProject ->
        kover(koverProject)
    }
}

val koverExcludedClasses = listOf(
    "net.opatry.h2go.app.di.*",
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
                        minValue = 0,
                        coverageUnits = CoverageUnit.INSTRUCTION
                    )
                }
                rule("Line coverage") {
                    minBound(
                        minValue = 0,
                        coverageUnits = CoverageUnit.LINE
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
}
