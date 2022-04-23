import kotlinx.datetime.Clock
import state.startupApplication

const val version = "v0.1.5"

suspend fun main() {
    println("ok $version " + (Clock.System.now()))
    startupApplication()
    OnewayApi.openWebSocket()
}

