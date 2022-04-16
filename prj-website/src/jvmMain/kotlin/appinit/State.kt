package appinit

import folders.Folders
import folders.data.etc.config
import java.io.File

class State(
    val folders: Folders,
) {
    val destroyCallback: MutableList<() -> Unit> = mutableListOf()
    val config = folders.config()
    val database = folders.initDatabasePart()
}

fun State.destroy() {
    destroyCallback.forEachIndexed { index, callback ->
        log.i("Running destroy callback ${index + 1}/${destroyCallback.size}")
        kotlin.runCatching { callback() }
    }
    log.i("contextDestroyed" + "=".repeat(100))
}

fun newState(): State = State(Folders(File(".")))