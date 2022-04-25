import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.datetime.Clock
import state.startupApplication

const val version = "v0.1.6"

suspend fun main() {
    println("ok $version " + (Clock.System.now()))
    println(window.location.href)
    console.log(document.currentScript)
    startupApplication()

    val l = window.location
    val u = l.protocol.replace("http", "ws") + l.host + l.pathname + "ws1"
    OnewayApi.openWebSocket(u)

}

