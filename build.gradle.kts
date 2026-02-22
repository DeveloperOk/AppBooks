// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "9.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false

    //KSP for Hilt and Room
    id("com.google.devtools.ksp") version "2.3.6" apply false

    //Hilt
    id("com.google.dagger.hilt.android") version "2.59.2" apply false

    //Navigation3
    kotlin("plugin.serialization") version "2.2.21" apply false

}