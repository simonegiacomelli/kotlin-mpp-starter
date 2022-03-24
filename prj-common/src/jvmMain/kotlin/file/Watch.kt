package file

import java.io.File
import java.nio.file.Files

class Watch(file: File) {
    private val path = file.toPath()
    private var state: String = newState()

    fun waitUntilChanged() {
        val old = state
        do {
            Thread.sleep(50)
            state = newState()
        } while (old == state)
    }

    private fun newState(): String {
        val size = Files.size(path)
        val mod = Files.getLastModifiedTime(path).toMillis()
        return "size:$size modifiedTime:$mod"
    }
}
