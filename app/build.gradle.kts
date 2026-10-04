plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val signingStoreFile = System.getenv("CAR_SERVICE_KEYSTORE_FILE")
val signingStorePassword = System.getenv("CAR_SERVICE_KEYSTORE_PASSWORD")
val signingKeyAlias = System.getenv("CAR_SERVICE_KEY_ALIAS")
val signingKeyPassword = System.getenv("CAR_SERVICE_KEY_PASSWORD")
val hasReleaseSigning = listOf(
    signingStoreFile,
    signingStorePassword,
    signingKeyAlias,
    signingKeyPassword
).all { !it.isNullOrBlank() }

val ciVersion = System.getenv("GITHUB_REF_NAME")
    ?.removePrefix("v")
    ?.takeIf { it.matches(Regex("\\d+(\\.\\d+){0,2}")) }
val appVersionName = providers.gradleProperty("versionName").orElse(ciVersion ?: "1.0.0").get()
val appVersionCode = appVersionName.split(".")
    .map { it.toIntOrNull() ?: 0 }
    .let { (it.getOrElse(0) { 1 } * 1_000_000) + (it.getOrElse(1) { 0 } * 1_000) + it.getOrElse(2) { 0 } }

android {
    namespace = "ru.carservice.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.carservice.app"
        minSdk = 26
        targetSdk = 35
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
        buildConfigField("String", "UPDATE_REPOSITORY", "\"zigorminsk-debug/CAR-SERVICE\"")
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(signingStoreFile!!)
                storePassword = signingStorePassword
                keyAlias = signingKeyAlias
                keyPassword = signingKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.mlkit:translate:17.0.3")

    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
}
