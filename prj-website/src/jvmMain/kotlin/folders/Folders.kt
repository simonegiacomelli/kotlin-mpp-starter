package folders

import java.io.File

class Folders(root: File) : File(root.canonicalPath) {
    val data: Data = Data(root.resolve("data").canonicalFile)
}

class Data(data: File) : File(data.canonicalPath) {
    val heapdump = resolve("heapdump").canonicalFile
    val proc = resolve("proc").canonicalFile
    val etc = resolve("etc").canonicalFile
    val html = resolve("html").canonicalFile
    val tmp = resolve("tmp").canonicalFile
}

