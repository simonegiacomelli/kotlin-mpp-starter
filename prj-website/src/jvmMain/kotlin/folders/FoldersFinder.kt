package folders

import java.io.File

/**
 * This class is used by support-tools (gradle tasks, etc.) and it
 * shall not be used by in the end-user binaries.
 *
 * The end-user binaries already knows which is the right folders
 * location. On the other hand, the support-tools may not be correct
 * in their assessment of the folders because the working directory
 * can change depending on how the support-tool is executed.
 */
class FoldersFinder {

    val repositoryRootFolder = scanDirectories(listOf(".", ".."))

    private fun scanDirectories(attempts: List<String>): File {
        val candidates = attempts.map { attempt -> File(attempt).canonicalFile }
        val result = candidates.firstOrNull { it.resolve("repository-root-marker.txt").exists() }
        return result ?: error("Repository root not found. Candidate paths:" +
                candidates.joinToString("\n") { "  $it" })
    }

    val folders: Folders = Folders(repositoryRootFolder.resolve("./prj-website").canonicalFile)
}
