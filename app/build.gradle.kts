plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.dicoding.picodiploma.storylensapp"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.dicoding.picodiploma.storylensapp"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
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
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.6.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.activity:activity-ktx:1.7.2")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")

    implementation("androidx.paging:paging-runtime-ktx:3.1.1")

    androidTestImplementation ("androidx.arch.core:core-testing:2.1.0") // InstantTaskExecutorRule
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1") // TestDispatcher

    testImplementation ("androidx.arch.core:core-testing:2.1.0") // InstantTaskExecutorRule
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1") // TestDispatcher
    testImplementation ("org.mockito:mockito-core:4.4.0")
    testImplementation ("org.mockito:mockito-inline:4.4.0")

    implementation ("com.google.android.gms:play-services-maps:18.0.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")


}