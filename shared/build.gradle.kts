plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.androidLibrary)
    id(libs.plugins.jetBrainCompose.get().pluginId)
    id(libs.plugins.composeCompiler.get().pluginId)
    alias(libs.plugins.kotlinKsp)
    alias(libs.plugins.androidxRoom)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.android.database.sqlcipher)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.ui.tooling.preview)
        }

        iosMain.dependencies {

        }

        iosMain {
            kotlin.srcDir("build/generated/ksp/metadata")
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.material3)
            implementation(compose.material)
            implementation(compose.materialIconsExtended)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.multiplatformSettings)
            implementation(libs.kstore)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.lifecycle.viewmodel)

            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }
        commonTest.dependencies {
            // implementation(libs.kotlin.test)
        }
    }
}

compose.resources {
    publicResClass = true
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    debugImplementation(libs.androidx.ui.tooling.v151)
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.notes.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    buildFeatures{
        compose = true
    }
}

//dependencies {
//    implementation(libs.androidx.ui.tooling.v151)
//}
