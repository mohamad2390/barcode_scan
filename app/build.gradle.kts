plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.tafavotco.samarapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tafavotco.samarapp"
        minSdk = 27
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //    retrofit
    implementation (libs.retrofit2.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.adapter.rxjava2)
    implementation (libs.logging.interceptor)

    implementation (libs.okhttp)

//    glide
    implementation (libs.glide)

    implementation (libs.zxing.android.embedded)

    implementation (libs.barcode.scanning)
    implementation (libs.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.camera.lifecycle)
    implementation (libs.androidx.camera.view)

//    sweetAlert
//    implementation (libs.android.sweetalert2)
}