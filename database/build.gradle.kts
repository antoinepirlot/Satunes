plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("androidx.room") version ("2.6.1")
    kotlin("plugin.serialization") version "1.9.22"
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "io.github.antoinepirlot.satunes.database"
    compileSdk = 34

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
     * Serialization to JSON
     */
    implementation(libs.kotlinx.serialization.json)

    /**
     * Mutable State
     */
    implementation(platform(libs.compose.bom))
    implementation(libs.material3)

    /**
     * Media
     */
    implementation(libs.androidx.media3.common)

    /**
     * DataStore
     */
    implementation(libs.androidx.datastore.preferences)

    /**
     * Database Android Room
     */
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    // To use Kotlin Symbol Processing (KSP)
    ksp(libs.androidx.room.compiler)

    /**
     * ID3 tags
     */
    implementation(libs.mp3agic)

    /**
     * Icons
     */
    implementation(project(":icons"))

    /**
     * Utils
     */
    implementation(project(":utils"))
}