plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.group8.busbookingapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.group8.busbookingapp"
        minSdk = 24
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
    buildFeatures {
        dataBinding = true
        viewBinding = true
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

    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation ("org.json:json:20230227")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.4")
    implementation ("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.android.volley:volley:1.2.1")

    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")



}