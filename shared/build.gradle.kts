plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "earth.mp3player.shared"
    compileSdk = 34

    defaultConfig {
        minSdk = 32
        targetSdk = 34

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
    implementation("androidx.media:media:1.7.0")

    /**
     * Playback
     */
    implementation(project(":playback"))
}