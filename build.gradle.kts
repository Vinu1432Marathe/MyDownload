import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.video.download.vidlink"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.video.download.vidlink"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    bundle {
        density {
            enableSplit = false
        }
        language {
            enableSplit = false
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // for design
    runtimeOnly(libs.material.v1130alpha11)

    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.google.android.exoplayer:exoplayer:2.19.0")


    // todo Ads........

    //todo for In App Rate
    implementation ("com.google.android.play:review:2.0.1")
    implementation ("com.google.android.play:review-ktx:2.0.0")

//    // todo for the Adssss
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.firebase.config)

    implementation("com.facebook.android:facebook-android-sdk:17.0.0")
    implementation("com.onesignal:OneSignal:[5.0.0, 5.99.99]")
    implementation("com.appsflyer:af-android-sdk:6.14.0")


    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.3.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation(project(":newmysdk"))
    implementation ("com.google.android.gms:play-services-ads:22.6.0")

}