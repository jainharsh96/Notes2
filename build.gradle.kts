//// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.compose.gradle.plugin)
    }
}
plugins {
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidKmpLibrary) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.composeCompiler) apply false
}

// run -> ./gradlew assembleRelease -Pmyapp.enableComposeCompilerReports=true --rerun-tasks
subprojects {
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java).configureEach {
        compilerOptions {
            if (project.findProperty("myapp.enableComposeCompilerReports") == "true") {
                freeCompilerArgs.addAll(
                    listOf(
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                                project.buildDir.absolutePath + "/compose_metrics",
                    )
                )
                freeCompilerArgs.addAll(
                    listOf(
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                                project.buildDir.absolutePath + "/compose_metrics",
                    )
                )
            }
        }
    }
}

