plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "io.nocora"
    compileSdk = 37
    buildToolsVersion = "37.0.0"
    buildFeatures { compose = true }
    defaultConfig {
        applicationId = "io.nocora"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.material3:material3:1.5.0-alpha20")
    implementation("androidx.core:core-splashscreen:1.2.0")
    implementation("androidx.datastore:datastore-preferences:1.3.0-alpha09")
    implementation("com.google.android.material:material:1.14.0")
    api("com.artifex.mupdf:fitz:1.27.1")
}
