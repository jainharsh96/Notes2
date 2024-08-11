//// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.hilt.android.gradle.plugin)
        classpath(libs.compose.gradle.plugin)
    }
}
plugins {
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.kotlinKsp).apply(false)
    alias(libs.plugins.composeCompiler).apply(false)
    alias(libs.plugins.androidxRoom).apply(false)
}

// run -> ./gradlew assembleRelease -Pmyapp.enableComposeCompilerReports=true --rerun-tasks
subprojects {
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
        kotlinOptions {
            if (project.findProperty("myapp.enableComposeCompilerReports") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                            project.buildDir.absolutePath + "/compose_metrics"
                )
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                            project.buildDir.absolutePath + "/compose_metrics"
                )
            }
        }
    }
}
