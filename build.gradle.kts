buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-alpha08")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
}

