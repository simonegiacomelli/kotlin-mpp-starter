import kotlinx.browser.window
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event
import rpc.OnewayContextHandlers
import rpc.RpcMessage
import rpc.oneway.Transport
import rpc.oneway.topics.BrowserTopic

val onewayContextHandler = OnewayContextHandlers<Any>()
val browserTopic = BrowserTopic<Any>(OnewayApi).apply { register(onewayContextHandler) }


object OnewayApi : Transport() {

    private fun calcWebsocketUrl(): String {
        val protocol = if (window.location.protocol == "https:") "wss" else "ws"
        val webSocketUrl = "$protocol://${window.location.host}/ws1"
        return webSocketUrl
    }

    private lateinit var socket: WebSocket

    override fun dispatcherOneway(apiName: String, payload: String) {
        socket.send(RpcMessage.encode(apiName, payload))
    }

    fun openWebSocket(url: String = calcWebsocketUrl()) {
        socket = WebSocket(url)
        println("openWebSocket url: ${socket.url}")
        socket.onmessage = { event ->
            val data = event.data as String
            println("onMessage data=$data")
            onewayContextHandler.dispatchOneway(data, Any())
        }
        socket.onopen = { log("onopen", it) }
        socket.onclose = { log("onclose", it) }
        socket.onerror = { log("onerror", it) }
        socket.addEventListener("open", { onOpen(it) })
    }

    var onOpen: (Event) -> Unit = {}
    private fun log(name: String, event: Event) {
        println(name)
        console.log(event)
    }
}