plugins {
    id("com.android.application")
}

android {
    compileSdk = 36

    defaultConfig {
        applicationId = "com.refresh.pos"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.txt")
        }
    }
    namespace = "com.refresh.pos"
}

dependencies {
    implementation("androidx.gridlayout:gridlayout:1.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.core:core-ktx:1.18.0")
    implementation(files("libs/achartengine-1.1.0.jar"))
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
}
