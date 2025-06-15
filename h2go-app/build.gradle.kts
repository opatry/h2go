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

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose.compiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

val versionCodeValue = System.getenv("CI_BUILD_NUMBER")?.toIntOrNull() ?: 1

android {
    namespace = "net.opatry.h2go"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "net.opatry.h2go"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = versionCodeValue
        versionName = libs.versions.h2go.get()

        androidResources.localeFilters += listOf("en", "fr")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("dev") {
            storeFile = file("dev.keystore")
            storePassword = "devdev"
            keyAlias = "dev"
            keyPassword = "devdev"
        }
        create("store") {
            val keystoreFilePath = findProperty("playstore.keystore.file") as? String
            storeFile = keystoreFilePath?.let(::file)
            storePassword = findProperty("playstore.keystore.password") as? String
            keyAlias = "h2go_android"
            keyPassword = findProperty("playstore.keystore.key_password") as? String
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true

            signingConfig = signingConfigs.getByName("dev")
        }
        getByName("release") {
            // we allow dev signing config in release build when not in CI to allow release builds on dev machine
            val ciBuild = (findProperty("ci") as? String).toBoolean()
            signingConfig = if (signingConfigs.getByName("store").storeFile == null && !ciBuild) {
                signingConfigs.getByName("dev")
            } else {
                signingConfigs.getByName("store")
            }

            isMinifyEnabled = true
            isShrinkResources = true
        }
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(projects.preferences)
    implementation(projects.onboarding)

    implementation(libs.kotlinx.coroutines.android) {
        because("requires Dispatchers.Main & co at runtime for Android")
        // java.lang.IllegalStateException: Module with the Main dispatcher is missing. Add dependency providing the Main dispatcher, e.g. 'kotlinx-coroutines-android' and ensure it has the same version as 'kotlinx-coroutines-core'
        // see also https://github.com/JetBrains/compose-jb/releases/tag/v1.1.1
    }

    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.androidx.startup)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)

    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)

    implementation(libs.kotlinx.serialization)

    implementation(libs.androidx.room.runtime)

    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    androidTestImplementation(projects.androidTestUtil)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.runner)

    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.junit4)
    androidTestImplementation(libs.assertj.core)

    testImplementation(libs.junit)
    testImplementation(libs.assertj.core)
    testImplementation(libs.koin.test)
}
