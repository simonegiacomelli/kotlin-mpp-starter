package rpc.oneway.topics

import Folders
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import java.io.File

open class PoolChange(val wsEndpoint: WsEndpoint)
class Add(ep: WsEndpoint) : PoolChange(ep)
class Remove(ep: WsEndpoint) : PoolChange(ep)

class WsEndpointPool {
    private val lock = Any()
    private val watchdog = Watchdog()

    private val pool = mutableSetOf<WsEndpoint>()
    val listeners = mutableSetOf<(PoolChange) -> Unit>()

    fun initialize() {
        writeSummaryFile()
    }

    fun add(client: WsEndpoint) = lock { pool.add(client); notify(Add(client)) }
    fun remove(client: WsEndpoint) = lock { pool.remove(client); notify(Remove(client)) }

    private fun notify(change: PoolChange) {
        writeSummaryFile()
        listeners.forEach { kotlin.runCatching { it(change) } }
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

}


class Watchdog() {
    private val writeFileChannel = Channel<String>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST) {

    }
    var stop = false

    init {
        Thread { runBlocking { processLoop() } }
            .apply {
                name = this::class.simpleName
                isDaemon = true
            }.start()
    }

    private suspend fun processLoop() {
        while (!stop) {
            try {
                process()
            } catch (ex: Throwable) {
                println(ex.stackTraceToString())
                delay(60000)
            }

        }
    }

    private suspend fun process() {
        val text = writeFileChannel.receive()
        txtPoolFile().writeText(text)
    }

    private fun txtPoolFile(): File {
        val proc = Folders.data.resolve("proc")
        proc.mkdirs()
        return proc.resolve("websocket-pool.txt")
    }

    suspend fun writeSummaryAsync(text: String) {
        writeFileChannel.send(text)
    }
}

val wsEndpointPool = WsEndpointPool()