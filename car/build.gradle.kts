plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.antoinepirlot.satunes.car"
    compileSdk = 35

    defaultConfig {
        minSdk = 22

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    buildTypes {
        create("releaseTest") {
            initWith(getByName("release"))
        }
    }
}

dependencies {
    /**
     * Base
     */
    implementation("androidx.activity:activity-compose:1.9.1")

    /**
     * Media
     */
    val mediaVersion = "1.7.0"
    val media3Version = "1.4.0"
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