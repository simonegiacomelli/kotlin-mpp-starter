package webcontext

import Folders
import heap.HeapDumper

class ContextInit(
    val folders: Folders,
//    val config: Config,
//    val jdbc: Jdbc,
//    val exposed: Exposed,
    val destroyCallback: MutableList<() -> Unit> = mutableListOf()
)

fun ContextInit.init() {
    HeapDumper.enableHeapDump(folders.data.heapdump)

    initKotlinPart()
}

fun ContextInit.destroy() {
    destroyCallback.forEachIndexed { index, callback ->
        log.i("Running destroy callback ${index + 1}/${destroyCallback.size}")
        kotlin.runCatching { callback() }
    }
    log.i("contextDestroyed" + "=".repeat(100))
}