plugins {
    alias(libs.plugins.kotlinKsp)
    alias(libs.plugins.multiplatform)
    id(libs.plugins.jetBrainCompose.get().pluginId)
    id(libs.plugins.composeCompiler.get().pluginId)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqlDelight)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
                }
            }
        }
    }
}

android {
    namespace = "com.harsh.notes"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.harsh.notes"
        minSdk = 23
        targetSdk = 36
        versionCode = 2
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    sourceSets["main"].apply {
        manifest.srcFile("src/main/AndroidManifest.xml")
        res.srcDirs("src/main/resources")
        resources.srcDirs("src/main/resources")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            //  signingConfig = signingConfigs.debug()
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }

//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.google.accompanist)

    // testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // compose
    val compose_version = "1.5.3"
    implementation(libs.androidx.compiler)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.ui)
    // Tooling support (Previews, etc.)
    implementation(libs.androidx.ui.tooling.v151)
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation(libs.androidx.foundation)
    // Material Design
    implementation(libs.androidx.material)
    // Material design icons
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)
    // Integration with activities
    implementation(libs.androidx.activity.compose)
    // Integration with ViewModels
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Integration with observables
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.runtime.rxjava2)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // compose navigation
    implementation(libs.androidx.navigation.compose)

    //sqlcipher
    implementation(libs.androidx.sqlite.ktx)
    implementation(libs.android.database.sqlcipher)

    //biometric
    implementation(libs.androidx.biometric)

    // UI Tests
    androidTestImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.mockito.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    // workmanager
    implementation(libs.androidx.work.runtime.ktx)

    // Google sign-in
    implementation(libs.play.services.auth) // Google Sign-In
    implementation(libs.google.api.services.drive) // Drive API
    implementation(libs.google.http.client.android)
    implementation(libs.google.api.client.android)

    // Car App
    implementation(libs.androidx.carapp)
    implementation(libs.androidx.carapp.projected)

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view) // Preview + CameraView
}
