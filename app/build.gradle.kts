plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "ru.sirius.grable"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "ru.sirius.grable"
        minSdk = 26
        targetSdk = 36
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
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.viewpager2)
    implementation(project(":libs:network"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.activity:activity-ktx:1.12.0")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)
    implementation(project(":libs:di"))
    implementation(project(":libs:imageloader:coil"))
    implementation(project(":libs:tts:embedded"))
    implementation(project(":core:design"))
    implementation(project(":core:database"))
    implementation(project(":navigation:api"))
    implementation(project(":navigation:impl"))
    implementation(project(":feature:home:impl"))
    implementation(project(":feature:add_word:api"))
    implementation(project(":feature:add_word:impl"))
    implementation(project(":feature:learn:api"))
    implementation(project(":feature:learn:impl"))
    implementation(project(":feature:progress:api"))
    implementation(project(":feature:settings:impl"))
    implementation(project(":feature:playlist:api"))
    implementation(project(":feature:playlist:impl"))
    implementation(project(":feature:progress:impl"))
    implementation("com.facebook.shimmer:shimmer:0.5.0")
}