plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.antoinepirlot.satunes.car"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()

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
    implementation(libs.activity.compose)

    /**
     * Media
     */
    implementation(libs.androidx.media)
    implementation(libs.androidx.media3.common)

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