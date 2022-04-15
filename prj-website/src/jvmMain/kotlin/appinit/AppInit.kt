package appinit

import folders.Folders
import folders.data.etc.config
import heap.HeapDumper

class AppInit(
    val folders: Folders,
    val destroyCallback: MutableList<() -> Unit> = mutableListOf()
) {
    val config = folders.config()
}

fun AppInit.init() {
    HeapDumper.enableHeapDump(folders.data.heapdump)
    folders.initDatabasePart()
    initKotlinPart()
}

fun AppInit.destroy() {
    destroyCallback.forEachIndexed { index, callback ->
        log.i("Running destroy callback ${index + 1}/${destroyCallback.size}")
        kotlin.runCatching { callback() }
    }
    log.i("contextDestroyed" + "=".repeat(100))
}