plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val nameSpace: String = "io.github.antoinepirlot.satunes"

android {
    namespace = nameSpace

    compileSdk {
        version = release(libs.versions.android.compileSdk.get().toInt())
    }

    androidResources {
        generateLocaleConfig = true
    }


    defaultConfig {
        applicationId = nameSpace
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        /**
         * Build is: 3 first digits for version then the type of release and finally the last two digits is for the release type version
         * Releases numbers are:
         *  - Stable: 3
         *  - Preview: 2
         *  - Beta: 1
         *  - Alpha: 0
         * For example:
         * v4.0.0: 400300
         * v4.0.1: 401300
         * v4.1.2: 412300
         * v4.0.0-preview-5: 400205
         * v5.2.0-beta-2: 520102
         * v6.0.0-alpha-12: 600012
         */
        versionCode = 400003
        versionName = "4.0.0-alpha-3"

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
    implementation(libs.androidx.glance)

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
     * Internet
     */
    implementation(project(":internet"))

    /**
     * Utils
     */
    implementation(project(":utils"))
}
