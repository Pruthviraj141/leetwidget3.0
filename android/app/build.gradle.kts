import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("dev.flutter.flutter-gradle-plugin")
}

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("key.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.atharva.leetcode_streak"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = "27.0.12077973"
      
    defaultConfig {
        applicationId = "com.atharva.leetcode_streak"
        // Set minSdk to 23 as required by androidx.glance
        minSdk = 23
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    buildTypes {
        release {
            // Safe casting for signing config - only use if key.properties exists
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.create("release") {
                    keyAlias = keystoreProperties["keyAlias"] as? String ?: ""
                    keyPassword = keystoreProperties["keyPassword"] as? String ?: ""
                    storeFile = keystoreProperties["storeFile"]?.let { file(it.toString()) }
                    storePassword = keystoreProperties["storePassword"] as? String ?: ""
                }
            }
            
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        
        debug {
            // Debug builds don't need signing
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
}

flutter {
    source = "../.."
}

dependencies {
    implementation("androidx.glance:glance:1.2.0-beta01")
    implementation("androidx.glance:glance-appwidget:1.2.0-beta01")
}