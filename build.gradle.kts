// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:7.0.3") // versión del plugin de Gradle de Android
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31") // versión del plugin de Kotlin
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.38.1") // Hilt plugin
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}