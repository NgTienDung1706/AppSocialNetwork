plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "vn.tiendung.socialnetwork"
    compileSdk = 35

    defaultConfig {
        applicationId = "vn.tiendung.socialnetwork"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation ("me.relex:circleindicator:2.1.6")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    implementation ("com.squareup.picasso:picasso:2.5.2")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("io.socket:socket.io-client:2.0.1")
    implementation ("com.github.nkzawa:socket.io-client:0.6.0")
    //implementation 'io.socket:socket.io-client:2.0.0'
    implementation ("io.socket:engine.io-client:2.0.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.14.2")
    // CameraX Core
    implementation ("androidx.camera:camera-core:1.3.2")
    implementation ("androidx.camera:camera-lifecycle:1.3.2")
    implementation ("androidx.camera:camera-camera2:1.3.2")
    implementation ("androidx.camera:camera-view:1.3.2")
    //Cloudinary
    implementation("com.cloudinary:cloudinary-android:2.3.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}