import forms.settings.UiSettings
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.datetime.Clock
import rpci.rpciHarness
import state.startupApplication

const val version = "v0.2.1"

suspend fun main() {
    runCatching { rpciHarness() }
    println("ok $version " + (Clock.System.now()))
    println(window.location.href)
    console.log(document.currentScript)
    UiSettings.install()
    startupApplication()

    val l = window.location
    val u = l.protocol.replace("http", "ws") + l.host + l.pathname + "ws1"
    OnewayApi.openWebSocket(u)


}

