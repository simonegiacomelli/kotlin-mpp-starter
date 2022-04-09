package rpc.oneway.topics

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import log.logger

import java.io.File

open class PoolChange(val wsEndpoint: WsEndpoint)
class Add(ep: WsEndpoint) : PoolChange(ep)
class Remove(ep: WsEndpoint) : PoolChange(ep)

class WsEndpointPool(procFolder: File) {
    private val L = logger()
    private val lock = Any()
    private val watchdog = Watchdog(procFolder)

    private val pool = mutableSetOf<WsEndpoint>()
    val listeners = mutableSetOf<(PoolChange) -> Unit>()

    fun initialize() {
        L.i("initialize()")
        writeSummaryFile()
    }

    fun add(client: WsEndpoint) = lock { pool.add(client); notify(Add(client)) }
    fun remove(client: WsEndpoint) = lock { pool.remove(client); notify(Remove(client)) }

    private fun notify(change: PoolChange) {
        writeSummaryFile()
        listeners.forEach { runCatching<Unit> { it(change) } }
    }

    private fun <T> lock(function: () -> T): T {
        synchronized(lock) { return function() }
    }

    private fun writeSummaryFile() {
        val endpointListRepr = if (pool.isEmpty())
            "Pool is empty"
        else
            pool.joinToString("\n") { it.strRepr() }
        val text = "Composed ${Clock.System.now()} \n\n" + endpointListRepr
        runBlocking { watchdog.writeSummaryAsync(text) }
    }

    fun destroy() = run { L.i("WsEndpointPool.destroy()"); watchdog.destroy() }


}


class Watchdog(private val procFolder: File) {
    private val L = logger()
    private val writeFileChannel = Channel<String>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST) {
        L.i("Dropping requests")
    }

    private val thread =
        Thread {
            try {
                runBlocking { processLoop() }
            } catch (ignore: InterruptedException) {
                L.i("ignoring InterruptedException")
            }
            L.i("Exiting...")
        }.apply {
            name = L.fullName
            isDaemon = true
            start()
        }


    private suspend fun processLoop() {
        L.i("processLoop()")
        while (!thread.isInterrupted) {
            try {
                process()
            } catch (ex: Throwable) {
                val stop = ex is CancellationException || ex is InterruptedException
                L.i("processLoop() catch{} stop=$stop " + ex.stackTraceToString().replace("\n", "\\n"))
                if (stop)
                    return
                else
                    delay(60000)
            }

        }
    }

    private suspend fun process() {
        val text = writeFileChannel.receive()
        txtPoolFile().writeText(text)
    }

    private fun txtPoolFile(): File {
        procFolder.mkdirs()
        return procFolder.resolve("websocket-pool.txt")
    }

    suspend fun writeSummaryAsync(text: String) {
        writeFileChannel.send(text)
    }

    fun destroy() {
        thread.interrupt()
        thread.join()
    }
}

var wsEndpointPoolBacking: WsEndpointPool? = null
val wsEndpointPool get() = wsEndpointPoolBacking ?: error("Istanza WsEndpointPool non valorizzata")