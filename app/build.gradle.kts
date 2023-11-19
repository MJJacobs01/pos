plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId ="com.refresh.pos"
        minSdkVersion(21)
        targetSdkVersion(34)
    }

    buildTypes {
        release {
            isMinifyEnabled =false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }
    namespace = "com.refresh.pos"
}

dependencies {
    implementation ("androidx.gridlayout:gridlayout:1.0.0")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation (files("libs/achartengine-1.1.0.jar"))
    implementation (files("libs/android-integration-2.0-supportv4.jar"))
    implementation("androidx.core:core-ktx:+")
}
