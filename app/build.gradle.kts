plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.apollographql.apollo").version("2.4.6")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    defaultConfig {
        applicationId = "com.example.rocketreserver"
        minSdkVersion(23)
        targetSdkVersion(30)
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
        kotlinCompilerVersion =  "1.4.21"
        kotlinCompilerExtensionVersion = "1.0.0-alpha10"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xallow-jvm-ir-dependencies", "-Xskip-prerelease-check",
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
}

dependencies {
    implementation("androidx.compose.ui:ui:1.0.0-alpha10")
    // Tooling support (Previews, etc.)
    implementation("androidx.ui:ui-tooling:1.0.0-alpha07")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:1.0.0-alpha10")
    // Material Design
    implementation("androidx.compose.material:material:1.0.0-alpha10")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:1.0.0-alpha10")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-alpha10")
    implementation("dev.chrisbanes.accompanist:accompanist-coil:0.4.2")

    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.security:security-crypto:1.1.0-alpha03")

    implementation("com.apollographql.apollo:apollo-runtime:2.4.6")
    implementation("com.apollographql.apollo:apollo-coroutines-support:2.4.6")

    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}

apollo {
    generateKotlinModels.set(true)
}
