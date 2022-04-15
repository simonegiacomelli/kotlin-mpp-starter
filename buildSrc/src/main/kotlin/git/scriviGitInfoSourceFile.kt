package git

import org.gradle.api.Project
import java.io.File

fun Project.scriviGitInfoSourceFile() {

    println("=".repeat(30) + " project.scriviGitInfoSourceFile() " + "=".repeat(30))
    println("project.rootDir=" + project.rootDir)
    println("project.projectDir=" + project.projectDir)

    val info = gitInformation()
    println("gitInformation()=```$info```")
    println("=".repeat(30))
    return

    val srcGitFolder = project.rootDir.resolve("src/commonMain/kotlin/git")
    srcGitFolder.mkdirs()
    val sourceFilePath = srcGitFolder.resolve("GitInfo.kt")
    val quotes = "\"\"\""
    val fileContent = "val gitInfo = $quotes$info$quotes"
    if (!sourceFilePath.exists() || sourceFilePath.readText() != fileContent) {
        println("writing file " + sourceFilePath.canonicalPath)
        sourceFilePath.writeText(fileContent)
        val dt = java.time.format.DateTimeFormatter.ISO_DATE_TIME.format(java.time.LocalDateTime.now())
        srcGitFolder.resolve("GitInfoDate.kt").writeText("val gitInfoDate = $quotes$dt$quotes")
    }
}

private fun Project.execSystemCommand(command: String): String {
    println("project.execSystemCommand(`$command`) wf=`${projectDir}`")
    val stdout = java.io.ByteArrayOutputStream()
    project.exec {
        commandLine(command.split(" "));
        workingDir = projectDir
        standardOutput = stdout
    }
    val toString = stdout.toString()
    println("output=```$toString```")
    return toString
}

private fun Project.gitInformation(): String {
    val rev = execSystemCommand("git rev-parse HEAD").trim()
    val show = execSystemCommand("git show -q $rev")
    val status = execSystemCommand("git status . --untracked-files=no --porcelain")
    val hashes = status.split("\n")
        .map { it.drop(3) } // drop flags like: ' M '
        .filter { it.isNotEmpty() }
        .map { File(it).canonicalFile }
        .filter { it.exists() }
        .map { it.toRelativeString(projectDir) }
        .onEach { println("~".repeat(5) + " $it") }
        .joinToString("\n") { execSystemCommand("git hash-object $it").trim() + " $it" }
    return "\ngit-show:\n$show\ngit-status:\n$status\nhash:\n$hashes"
}

private fun Project.printGit() {
    val info = gitInformation()
    println("=".repeat(100))
    println(info)
    println("=".repeat(100))
}