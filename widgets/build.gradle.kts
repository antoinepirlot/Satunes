plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.antoinepirlot.satunes.widgets"
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
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
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
    /**
     * Base
     */
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    //Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    /**
     * Widget
     */
    // For AppWidgets support
    implementation(libs.glance.appwidget)

    // For interop APIs with Material 3
    implementation(libs.glance.material3)

    /**
     * Database
     */
    implementation(project(":database"))

    /**
     * Playback
     */
    implementation(project(":playback"))

    /**
     * Icons
     */
    implementation(project(":icons"))

    /**
     * Singleton, Inject
     */
    implementation(libs.javax.inject)

    /**
     * Utils
     */
    implementation(project(":utils"))
}