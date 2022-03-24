import java.nio.file.Paths


plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}
val ktor_version = "1.6.4"

repositories {
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
        withJava()
    }
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.3")
                implementation(project(":prj-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core:$ktor_version")
                implementation("io.ktor:ktor-server-cio:$ktor_version")
                implementation("io.ktor:ktor-websockets:$ktor_version")
                implementation("io.ktor:ktor-html-builder:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
                implementation("org.slf4j:slf4j-simple:1.7.30")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation(project(":prj-common"))
            }
        }
        val jsTest by getting
    }
}


fun copyJsToWebContent(dir: String) {
    val jsBuild = Paths.get("$buildDir/$dir").toFile()
    val targetDir = File("$projectDir/data/wwwroot/js/compiled")
    println("COPYING JS --------------------------------------")
    println("from $jsBuild")
    println("to $targetDir")
    targetDir.mkdirs()
    targetDir.listFiles()?.forEach { it.deleteRecursively() }
    jsBuild.copyRecursively(targetDir, true)
}

tasks.register("appJs") {
    group = "app-" + project.name
    dependsOn(tasks.getByName("jsBrowserDevelopmentWebpack"))
    doLast {
        copyJsToWebContent("developmentExecutable")
    }
}

tasks.register("appJsProd") {
    group = "app-" + project.name
    dependsOn(tasks.getByName("jsBrowserProductionWebpack"))
    doLast { copyJsToWebContent("distributions") }
}


tasks.createJavaExec("JvmMainKt", "appJvmExec")

fun TaskContainer.createJavaExec(mainClassFqdn: String, taskName: String? = null) {
    create<JavaExec>(taskName ?: mainClassFqdn) {
        group = "app-" + project.name
        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set(mainClassFqdn)
        standardInput = System.`in`
        workingDir = projectDir
//        standardOutput = System.out
//        errorOutput = System.err
    }
}

