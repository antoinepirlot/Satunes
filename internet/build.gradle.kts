plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization") version "1.9.22"
}

android {
    namespace = "io.github.antoinepirlot.satunes.internet"

    compileSdk {
        version = release(libs.versions.android.compileSdk.get().toInt())
    }

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()

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
     * Serialization to JSON
     */
    implementation(libs.kotlinx.serialization.json)

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