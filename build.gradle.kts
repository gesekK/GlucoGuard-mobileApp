buildscript {
    repositories {
        // Make sure that you have the following two repositories
        google()  // Google's Maven repository
        jcenter()
        mavenCentral()  // Maven Central repository
        maven { url = uri("https://www.jitpack.io") }


    }
    dependencies {
        // Add the dependency for the Google services Gradle plugin
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.github.PhilJay:MPAndroidChart:v3.1.0")

    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("com.android.library") version "8.0.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}