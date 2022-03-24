package html

import Folders
import api.oneway.ApiNotifyHtmlChange
import file.Watch
import rpc.oneway.OnewayContext
import rpc.oneway.Transport
import rpc.oneway.onewayContextHandler
import rpc.oneway.topics.BrowserTopic
import rpc.oneway.topics.WsEndpoint

fun signalHtml() {
    val endPoint = WsEndpoint()

    val transport = object : Transport() {
        override fun dispatcherOneway(apiName: String, payload: String) {
            onewayContextHandler.dispatchOneway(apiName, payload, OnewayContext(endPoint))
        }
    }
    val browserTopic = BrowserTopic<OnewayContext>(transport)

    val file = Folders.data.resolve("html/design.html")
    file.parentFile.mkdirs()
    if (!file.exists()) file.writeText("<h1>hello</h1>")
    val watch = Watch(file)
    while (true) {
        println("waiting for file to change ${file.canonicalPath}")
        watch.waitUntilChanged()
        browserTopic.publish(ApiNotifyHtmlChange(file.readText()))
    }
}