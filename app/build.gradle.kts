plugins {
    alias(libs.plugins.android.application)

}



android {
    namespace = "com.example.clexis"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.clexis"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.airbnb.android:lottie:6.4.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

// JSON converter (Gson)
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("io.realm:realm-android-library:10.16.1")

    compileOnly ("org.projectlombok:lombok:1.18.32")
    annotationProcessor ("org.projectlombok:lombok:1.18.32")

    implementation ("com.jakewharton.threetenabp:threetenabp:1.4.4")
    implementation ("org.dizitart:nitrite:4.3.0")


    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")





}