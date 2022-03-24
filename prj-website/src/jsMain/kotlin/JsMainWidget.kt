import keyboard.HotkeyWindow
import kotlinx.browser.document
import kotlinx.datetime.Clock
import pages.bootstrap.BootstrapHomeWidget
import pages.forms.HtmlSignalWidget
import widget.HolderWidget

val version = "v0.1.4"
fun jsMainWidget() {
    println("ok $version " + (Clock.System.now()))
//    GlobalScope.launch {
//        println(Api.send(ApiAddRequest(4, 5)).result)
//    }

    //window.addEventListener("load", { loadRootWidget() })
    loadRootWidget()
    OnewayApi.openWebSocket()
}

private fun loadRootWidget() {
    val container = document.getElementById("root") ?: document.body!!
    val widgetHolder = HolderWidget()
    container.append(widgetHolder.container)
    widgetHolder.show(BootstrapHomeWidget())
    HotkeyWindow
        .add("SHIFT-F3") { widgetHolder.show(HtmlSignalWidget.shared); it.stopPropagation(); it.preventDefault() }
}
