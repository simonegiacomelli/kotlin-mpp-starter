package appinit

import folders.Folders
import folders.data.etc.Config
import heap.HeapDumper
import jdbc.Jdbc
import org.jetbrains.exposed.sql.Database

class AppInit(
    val folders: Folders,
    val config: Config,
    val jdbc: Jdbc,
//    val exposed: Exposed,
    val database: Database,
    val destroyCallback: MutableList<() -> Unit> = mutableListOf()
)

fun AppInit.init() {
    HeapDumper.enableHeapDump(folders.data.heapdump)

    initKotlinPart()
}

fun AppInit.destroy() {
    destroyCallback.forEachIndexed { index, callback ->
        log.i("Running destroy callback ${index + 1}/${destroyCallback.size}")
        kotlin.runCatching { callback() }
    }
    log.i("contextDestroyed" + "=".repeat(100))
}