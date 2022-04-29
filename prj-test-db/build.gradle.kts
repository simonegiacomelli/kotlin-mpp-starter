plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

val exposed_version: String = "0.37.3"
val kotlin_version: String by rootProject.extra

//dependencies {
//    implementation(project(":prj-common"))
//    implementation(project(":prj-website"))
////    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.3")
////    implementation("mysql:mysql-connector-java:8.0.25")
////    testImplementation(kotlin("test"))
//}

kotlin {
    sourceSets {

        val main by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")

                implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
                implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
                implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")

                implementation(project(":prj-common"))
                implementation(project(":prj-website"))
            }
        }
        val test by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.xerial:sqlite-jdbc:3.36.0.3")
            }
        }
    }

}
tasks.test { useTestNG() }

//tasks.createJavaExec("schema.generator.main.GenerateInteractiveKt")

fun JavaExec.setup(mc: String) {
    group = "app-" + project.name// "application"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set(mc)
    standardInput = System.`in`
    workingDir = projectDir
//    standardOutput = System.out
//    errorOutput = System.err
}


fun TaskContainer.createJavaExec(mc: String) {
    create<JavaExec>(mc) { setup(mc) }
}

