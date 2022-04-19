import git.scriviGitInfoSourceFile
import java.nio.file.Paths

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

val ktor_version = "1.6.4"
val exposed_version: String = "0.37.3"

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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
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

                implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
                implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
                implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
                implementation("ch.qos.logback:logback-classic:1.2.3")

                implementation("org.postgresql:postgresql:42.3.3")
                implementation("org.xerial:sqlite-jdbc:3.36.0.3")

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

val appEtcCopy = "appEtcCopy"

tasks.createJavaExec(
    "JvmMainKt", "appJvmExec",
//    listOf("-Dio.ktor.development=true")
    args = listOf("start")
).apply {
    dependsOn(appEtcCopy)
}

tasks.createJavaExec(
    "JvmMainKt", "appCli",
).apply {
    dependsOn(appEtcCopy)
}

tasks.register<JavaExec>(appEtcCopy) {
    group = "app-" + project.name
    dependsOn(tasks.getByName("compileJava"))
    mainClass.set("folders.data.etc.CopyConfigKt")
    classpath = sourceSets["main"].runtimeClasspath
}
fun TaskContainer.createJavaExec(
    mainClassFqdn: String,
    taskName: String? = null,
    args: List<String>? = null,
    jvmArgs: List<String>? = null,
) = create<JavaExec>(taskName ?: mainClassFqdn) {
    group = "app-" + project.name
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set(mainClassFqdn)
    standardInput = System.`in`
    workingDir = projectDir
    if (args != null) this.args = args
    if (jvmArgs != null) this.jvmArgs = jvmArgs
//        standardOutput = System.out
//        errorOutput = System.err
}

// steps to test if cache is honored:
// 1) verify without any harness that a double compilation enjoy cache effects
// 2) with harness, a double compilation should enjoy cache effects
// 3) with harness, (a) after a compilation , (b) touch a file,
//    (c) look at correct gitinfo (d) next compile should not benefit on cache
tasks.register<Copy>("copyDocsTestGitinfo") {
    group = "app-" + project.name
    from("dev-data/auth")
    into("dev-data/auth-test-gitinfo")
    outputs.cacheIf { true }
}

scriviGitInfoSourceFile()