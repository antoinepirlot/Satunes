/*
 * This file is part of Satunes.
 *
 *  Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  **** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.antoinepirlot.satunes.car"
    compileSdk = 34

    defaultConfig {
        minSdk = 25

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    /**
     * Base
     */
    implementation("androidx.activity:activity-compose:1.8.2")

    /**
     * Media
     */
    val mediaVersion = "1.7.0"
    val media3Version = "1.3.1"
    implementation("androidx.media:media:$mediaVersion")
    implementation("androidx.media3:media3-common:$media3Version")

    /**
     * Playback
     */
    implementation(project(":playback"))

    /**
     * Database
     */
    implementation(project(":database"))

    /**
     * Icons
     */
    implementation(project(":icons"))

    /**
     * Utils
     */
    implementation(project(":utils"))
}