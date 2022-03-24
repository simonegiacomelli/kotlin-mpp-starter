import keyboard.HotkeyWindow
import kotlinx.browser.document
import kotlinx.datetime.Clock
import pages.bootstrap.BootstrapHomeWidget
import pages.forms.HtmlSignalWidget
import widget.HolderWidget

const val version = "v0.1.4"
fun main() {
    println("ok $version " + (Clock.System.now()))
    loadRootWidget()
    OnewayApi.openWebSocket()
}

private fun loadRootWidget() {
    val container = document.getElementById("root") ?: document.body!!
    val holder = HolderWidget()
    container.append(holder.container)
    holder.show(BootstrapHomeWidget())
    HotkeyWindow.add("SHIFT-F3") { holder.show(HtmlSignalWidget.shared) }
}
