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
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "net.opatry.h2go.preferences"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.room.common)

    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)

    ksp(libs.androidx.room.compiler)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.assertj.core)
    testImplementation(libs.androidx.room.runtime.jvm)
    testImplementation(libs.androidx.sqlite.bundled.jvm)
    testRuntimeOnly(libs.androidx.sqlite.jvm)
    implementation(libs.koin.test)
}
