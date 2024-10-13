import org.gradle.kotlin.dsl.test

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.trustandroid20"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.trustandroid20"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


            // Other configurations

            packagingOptions {
                exclude ("META-INF/DEPENDENCIES")
                exclude ("META-INF/DEPENDENCIES.txt")
                exclude ("META-INF/LICENSE")
                exclude ("META-INF/LICENSE.txt")
                exclude ("META-INF/NOTICE")
                exclude ("META-INF/NOTICE.txt")

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation ("androidx.compose.ui:ui:1.0.0")
    implementation ("androidx.compose.material:material:1.0.0")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.0.0")
    implementation ("androidx.activity:activity-compose:1.3.0")
    implementation ("androidx.webkit:webkit:1.4.0")
    implementation ("androidx.work:work-runtime-ktx:2.7.1")
    implementation ("androidx.appcompat:appcompat:1.3.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
// Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")


    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
    implementation(libs.androidx.core.ktx)

    // Kotlin Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // JSON Handling
    implementation ("org.json:json:20220320") // Or any preferred JSON library
    implementation ("androidx.compose.runtime:runtime-livedata:1.3.0")
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation ("com.google.firebase:firebase-firestore-ktx:24.1.1")
    implementation ("com.google.firebase:firebase-auth-ktx:21.0.1")
    implementation ("androidx.compose.ui:ui:1.1.0")
    implementation ("androidx.compose.material:material:1.1.0")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.1.0")
    implementation ("androidx.activity:activity-compose:1.4.0")
    implementation ("com.google.firebase:firebase-bom:28.4.2")
    implementation ("androidx.navigation:navigation-compose:2.5.3")
    implementation ("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("androidx.navigation:navigation-compose:2.8.2")
    implementation ("androidx.compose.runtime:runtime-livedata:1.3.0")
}