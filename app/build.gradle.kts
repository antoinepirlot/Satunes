plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val nameSpace: String = "io.github.antoinepirlot.satunes"

android {
    namespace = nameSpace
    compileSdk = 35

    androidResources {
        generateLocaleConfig = true
    }


    defaultConfig {
        applicationId = nameSpace
        minSdk = 22
        targetSdk = 35
        versionCode = 78
        versionName = "3.0.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("releaseTest") {
            initWith(getByName("release"))
            applicationIdSuffix = ".test"
            resValue(type = "string", name = "app_name", value = "${rootProject.name} (test)")
        }

        debug {
            applicationIdSuffix = ".debug"
            resValue(type = "string", name = "app_name", value = "${rootProject.name} (debug)")
        }
    }
    buildFeatures {
        compose = true
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

    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }
}

dependencies {

    /**
     * Base
     */
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.activity.compose)
    implementation(libs.fragment.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.accompanist.permissions)

    //Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    /**
     * Media
     */
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.session)

    /**
     * Icons
     */
    implementation(project(":icons"))

    /**
     * Navigation
     */
    // Kotlin
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Jetpack Compose Integration
    implementation(libs.androidx.navigation.compose)

    /**
     * DataStore
     */
    implementation(libs.androidx.datastore.preferences)

    /**
     * Widget
     */
    // For AppWidgets support
    implementation(libs.glance.appwidget)

    // For interop APIs with Material 3
    implementation(libs.glance.material3)

    /**
     * Singleton, Inject
     */
    implementation(libs.javax.inject)

    /**
     * Android Auto
     */
    implementation(project(":car"))

    /**
     * Libs
     */
    implementation(project(":libs:components"))

    /**
     * Playback Services
     */
    implementation(project(":playback"))

    /**
     * Database
     */
    implementation(project(":database"))

    /**
     * Update
     */
    implementation(project(":internet"))

    /**
     * Utils
     */
    implementation(project(":utils"))
}
