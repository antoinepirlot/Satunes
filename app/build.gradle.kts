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
        versionCode = 44
        versionName = "2.1.0"

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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    /**
     * Base
     */
    val composeUiVersion = "1.6.8"
    val composeBomVersion = "2024.06.00"
    val lifeCycleVersion = "2.8.4"
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifeCycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifeCycleVersion")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.ui:ui:$composeUiVersion")
    implementation("androidx.compose.ui:ui-graphics:$composeUiVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeUiVersion")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("com.google.accompanist:accompanist-permissions:0.31.0-alpha")

    //Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    /**
     * Media
     */
    val media3Version = "1.4.0"

    implementation("androidx.media3:media3-common:$media3Version")
    implementation("androidx.media3:media3-session:$media3Version")

    /**
     * Icons
     */
    implementation(project(":icons"))

    /**
     * Navigation
     */
    val navVersion = "2.7.7"
    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // Jetpack Compose Integration
    implementation("androidx.navigation:navigation-compose:$navVersion")


    /**
     * DataStore
     */
    val dataStoreVersion = "1.1.1"
    implementation("androidx.datastore:datastore-preferences:$dataStoreVersion")

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
