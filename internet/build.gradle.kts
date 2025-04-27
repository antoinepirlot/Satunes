/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.antoinepirlot.satunes.internet"
    compileSdk = 35

    defaultConfig {
        minSdk = 22

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("releaseTest") {
            initWith(getByName("release"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    /**
     * Mutable State
     */
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.compose.material3.material3)

    /**
     * OkHttp3
     */
    implementation(libs.okhttp)

    /**
     * Utils
     */
    implementation(project(":utils"))

    /**
     * Database
     */
    implementation(project(":database"))
}