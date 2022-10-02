plugins {
    kotlin("multiplatform") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    }
}

kotlin {
    jvm { }
}


tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().configureEach {
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xopt-in=kotlin.time.ExperimentalTime",
        "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}
