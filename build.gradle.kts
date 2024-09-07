// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {

        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")

    }

}


plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false

    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
    id("com.google.dagger.hilt.android") version "2.48" apply false

}

