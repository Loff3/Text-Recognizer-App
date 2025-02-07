plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.Text_Recognizer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.Text_Recognizer"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true

    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.protolite.well.known.types)
    implementation(libs.generativeai)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidbrowserhelper)
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // splShscreen
    implementation(libs.androidx.core.splashscreen.v100beta02)


    // To recognize Latin script
    implementation(libs.text.recognition)
    // CameraX

    implementation(libs.androidx.camera.core.v123)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.video)

    implementation (libs.androidx.camera.view.v123)
    implementation (libs.androidx.camera.extensions)

    //pdf
    implementation(libs.itext7.core)

    //splashscreen
    implementation(libs.androidx.core.splashscreen)

    // Shaders
    implementation(libs.hypnoticcanvas)
    implementation(libs.hypnoticcanvas.shaders)

    // icons

    implementation(libs.icons.lucide)


    // animation transissions
    implementation(libs.ui)
    implementation(libs.androidx.navigation.compose.v270beta01)



}