import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Pincode-hash, salt en ontgrendelduur komen uit blocker.properties (niet in git).
// Aanmaken of wijzigen: tools/New-PinHash.ps1
val blocker = Properties().apply {
    val file = rootProject.file("blocker.properties")
    if (!file.exists()) {
        throw GradleException(
            "blocker.properties ontbreekt. Maak het aan met: powershell -File tools/New-PinHash.ps1"
        )
    }
    file.inputStream().use { load(it) }
}

fun blockerProp(key: String): String =
    blocker.getProperty(key)?.trim()?.takeIf { it.isNotEmpty() }
        ?: throw GradleException("blocker.properties mist de sleutel '$key'.")

android {
    namespace = "nl.playstoreblocker"
    compileSdk = 35

    defaultConfig {
        applicationId = "nl.playstoreblocker"
        // Galaxy Tab A9 komt met Android 13 (API 33) uit de doos.
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"

        buildConfigField("String", "PIN_SALT", "\"${blockerProp("pinSalt")}\"")
        buildConfigField("String", "PIN_HASH", "\"${blockerProp("pinHash")}\"")
        buildConfigField("int", "PIN_ITERATIONS", blockerProp("pinIterations"))
        buildConfigField("int", "UNLOCK_MINUTES", blockerProp("unlockMinutes"))
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}
