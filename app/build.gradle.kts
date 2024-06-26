plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.8.10"
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
}

android {
    namespace = "cgodin.qc.ca"
    compileSdk = 34

    defaultConfig {
        applicationId = "cgodin.qc.ca"
        minSdk = 29
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    kapt {
        generateStubs = true
    }
}

dependencies {

    //navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.5")

    // Room components
    implementation ("androidx.room:room-runtime:2.6.1")
    //noinspection KaptUsageInsteadOfKsp
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.21-1.0.15")


    // Kotlin Extensions and Coroutines support for Room
    implementation ("androidx.room:room-ktx:2.6.1")

    // RxJava support for Room
    implementation ("androidx.room:room-rxjava2:2.6.1")

    // Guava support for Room, including Optional and ListenableFuture
    implementation ("androidx.room:room-guava:2.6.1")

    // SwipeRrefreshLayout
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Retrofit for network communication
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation ("com.squareup.retrofit2:adapter-rxjava2:2.9.0")


    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}