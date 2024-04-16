plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "earth.mp3player.car"
    compileSdk = 34

    defaultConfig {
        minSdk = 29

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
}