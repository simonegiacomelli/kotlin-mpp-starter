package folders.data.etc

import folders.FoldersFinder
import host.Hostname


fun main() {

    val finder = FoldersFinder()
    val src = finder.folders.resolve("dev-data/etc").canonicalFile
    val dst = finder.folders.data.etc
    println("=".repeat(30) + " Copy development etc files " + "=".repeat(30))
    println("from `$src`\nto   `$dst`")

    val list = listOf(
        configFilename to configFilename,
        Hostname.get() + "/" + configFilename to Hostname.get() + ".properties"
    )
    list.forEach { entry ->
        val srcFilename = entry.first
        val dstFilename = entry.second
        val srcFile = src.resolve(srcFilename).canonicalFile
        val dstFile = dst.resolve(dstFilename).canonicalFile
        if (srcFile.exists()) {
            println("  Copy $srcFilename to $dstFilename")
            srcFile.copyTo(dstFile, overwrite = true)
        } else {
            println("  Skip file $srcFilename (it does not exist) to $dstFilename")
        }
    }

}