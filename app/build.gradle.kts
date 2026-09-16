plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.victozee.kodeboard"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.victozee.kodeboard"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    useLibrary("android.test.runner")
    useLibrary("android.test.base")
    useLibrary("android.test.mock")

    lint {
        checkReleaseBuilds = false
    }

    // Kotlin sources live under src/*/kotlin
    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin")
        }
        getByName("test") {
            java.srcDirs("src/test/kotlin")
        }
        getByName("androidTest") {
            java.srcDirs("src/androidTest/kotlin")
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.preference:preference:1.2.1")
    implementation("com.google.android.material:material:1.14.0")

    // AppIntro
    implementation("com.github.AppIntro:AppIntro:6.3.1")

    // Colour picker
    implementation("com.github.evilbunny2008:android-material-color-picker-dialog:1.3.12")

    // Kotlin stdlib
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.21")
    implementation("androidx.core:core-ktx:1.13.1")

    // Required for local unit tests (JUnit 4 framework)
    testImplementation("junit:junit:4.13.2")

    implementation("androidx.annotation:annotation:1.10.0")
    androidTestImplementation("androidx.annotation:annotation:1.10.0")

    androidTestImplementation("androidx.test:core:1.7.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
}
