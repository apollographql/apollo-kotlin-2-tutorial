plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.apollographql.apollo").version("2.3.1")
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.3")

    defaultConfig {
        applicationId = "com.example.rocketreserver"
        minSdkVersion(23)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-alpha01"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.compose.ui:ui:1.0.0-alpha01")
    // Tooling support (Previews, etc.)
    implementation("androidx.ui:ui-tooling:1.0.0-alpha01")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:1.0.0-alpha01")
    // Material Design
    implementation("androidx.compose.material:material:1.0.0-alpha01")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:1.0.0-alpha01")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-alpha01")
    implementation("dev.chrisbanes.accompanist:accompanist-coil:0.2.0")

    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha02")

    implementation("com.apollographql.apollo:apollo-runtime:2.3.1")
    implementation("com.apollographql.apollo:apollo-coroutines-support:2.3.1")

    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}

apollo {
    generateKotlinModels.set(true)
}