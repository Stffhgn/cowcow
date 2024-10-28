// C:\Users\steff\AndroidStudioProjects\Cow_Cow\app\build.gradle.kts

plugins {
    // Applying essential plugins for Android app development and Kotlin support
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    // SafeArgs plugin for type-safe navigation
    id("androidx.navigation.safeargs.kotlin")
}

android {
    // The namespace for the app's code
    namespace = "com.example.cow_cow"
    // The SDK version the app will compile against
    compileSdk = 34

    defaultConfig {
        // Unique application ID
        applicationId = "com.example.cow_cow"
        // Minimum and target SDK versions for the app
        minSdk = 24
        targetSdk = 34
        // Versioning for the app
        versionCode = 1
        versionName = "1.0"
        // Test runner for instrumented tests
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Enabling support for vector drawables on older Android versions
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Enabling various build features
    buildFeatures {
        // Enable Data Binding
        dataBinding = true
        // Enable Jetpack Compose for UI development
        compose = true
    }

    // Configuring options for Jetpack Compose
    composeOptions {
        // Set the version of Kotlin compiler extension to match the project settings
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    buildTypes {
        // Configuration for the release build type
        release {
            // Disabling code shrinking and obfuscation for the release version
            isMinifyEnabled = false
            // ProGuard configuration files for the release version
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Enabling ViewBinding across the project for easy interaction with views
    viewBinding {
        enable = true
    }

    // Compile options to specify Java compatibility versions
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // Configuring Kotlin options for JVM compatibility
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Packaging options to exclude unnecessary files from being packaged in the APK
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Dependencies for Jetpack Compose (UI development)
    implementation("androidx.compose.ui:ui:1.5.1")
    implementation("androidx.compose.material3:material3:1.1.0") // Material 3 Dependency
    implementation("androidx.compose.material:material:1.5.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1")
    implementation("androidx.activity:activity-compose:1.7.2")

    // Debugging tools for Jetpack Compose
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.1")

    // Flexbox Layout
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // Compose LiveData integration
    implementation("androidx.compose.runtime:runtime-livedata:1.5.1")

    // Core and lifecycle libraries for Kotlin extensions and lifecycle-aware components
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // Fragment extensions to make working with fragments easier
    implementation("androidx.fragment:fragment-ktx:1.5.2")

    // JSON parsing using Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Material design components (buttons, cards, dialogs, etc.)
    implementation("com.google.android.material:material:1.9.0")

    // ConstraintLayout for flexible and dynamic UI layouts
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // DrawerLayout for navigation drawers
    implementation("androidx.drawerlayout:drawerlayout:1.1.1")

    // Kotlin Standard Library (JDK 7 compatibility)
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0")

    // JUnit for unit testing
    testImplementation(libs.junit)

    // AndroidX Test libraries for instrumented tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Navigation components for handling navigation between fragments
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
}
