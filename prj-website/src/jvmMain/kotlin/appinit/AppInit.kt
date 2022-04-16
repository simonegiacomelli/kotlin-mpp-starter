package appinit

import folders.Folders
import folders.data.etc.config

class AppInit(
    val folders: Folders,
    val destroyCallback: MutableList<() -> Unit> = mutableListOf()
) {
    val config = folders.config()
    val database = folders.initDatabasePart()

    init {
        initKotlinPart()
    }
}

fun AppInit.destroy() {
    destroyCallback.forEachIndexed { index, callback ->
        log.i("Running destroy callback ${index + 1}/${destroyCallback.size}")
        kotlin.runCatching { callback() }
    }
    log.i("contextDestroyed" + "=".repeat(100))
}