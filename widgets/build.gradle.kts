plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
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
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    //Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    /**
     * Widget
     */
    val widgetVersion = "1.1.0"
    // For AppWidgets support
    implementation("androidx.glance:glance-appwidget:$widgetVersion")

    // For interop APIs with Material 3
    implementation("androidx.glance:glance-material3:$widgetVersion")

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
    implementation("javax.inject:javax.inject:1")

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    /**
     * Utils
     */
    implementation(project(":utils"))
}