import java.io.File

class Folders(val data: Data)

class Data(data: File) : File(data.canonicalPath) {
    val heapdump = resolve("heapdump").canonicalFile
    val proc = resolve("proc").canonicalFile
    val etc = resolve("etc").canonicalFile
}