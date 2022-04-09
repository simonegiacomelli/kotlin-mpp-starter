package html

import api.oneway.ApiNotifyHtmlChange
import file.Watch
import org.slf4j.LoggerFactory
import rpc.oneway.OnewayContext
import rpc.oneway.Transport
import rpc.oneway.onewayContextHandler
import rpc.oneway.topics.BrowserTopic
import rpc.oneway.topics.WsEndpoint
import java.io.File

class SignalHtmlThread(private val fileToWatch: File) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val thread = Thread {
        try {
            signalHtml()
        } catch (ignore: InterruptedException) {
        }
        log.info("SignalHtmlThread exiting...")
    }.apply {
        name = "signalHtml"
        isDaemon = true
        start()
    }

    fun destroy() {
        log.info("SignalHtmlThread destroy()")
        thread.interrupt()
        thread.join()
    }

    private fun transport() = object : Transport() {
        val endPoint = WsEndpoint()
        override fun dispatcherOneway(apiName: String, payload: String) {
            onewayContextHandler.dispatchOneway(
                apiName, payload,
                OnewayContext(endPoint)
            )
        }

    }

    private fun signalHtml() {

        val browserTopic = BrowserTopic<Any>(transport())


        fileToWatch.parentFile.mkdirs()
        if (!fileToWatch.exists()) fileToWatch.writeText("<h1>hello</h1>")
        val watch = Watch(fileToWatch)
        while (!thread.isInterrupted) {
            log.info("SignalHtmlThread waiting for file to change ${fileToWatch.canonicalPath}")
            watch.waitUntilChanged()
            browserTopic.publish(ApiNotifyHtmlChange(fileToWatch.readText()))
        }
    }
}

