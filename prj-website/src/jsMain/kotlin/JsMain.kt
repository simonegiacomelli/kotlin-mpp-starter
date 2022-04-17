import api.names.ApiTmEventRequest
import forms.login.LoginWidget
import keyboard.HotkeyWindow
import kotlinx.browser.document
import kotlinx.datetime.Clock
import pages.bootstrap.*
import pages.forms.HtmlSignalWidget
import rpc.send
import state.installClientHandler
import utils.launchJs
import widget.HolderWidget

const val version = "v0.1.4"
fun main() {
    println("ok $version " + (Clock.System.now()))
    installClientHandler()
    loadRootWidget()
    OnewayApi.openWebSocket()
}

private fun loadRootWidget() {
    val container = document.getElementById("root") ?: document.body!!
    val bodyHolder = HolderWidget()
    container.append(bodyHolder.container)
    HotkeyWindow.log_prefix = "HotkeyWindow"
    val loginWidget = LoginWidget()
    val mainWidget = MainWidget()

    val bootrapWidget = NavbarWidget()
    val offcanvasWidget = OffcanvasWidget()
    offcanvasWidget.setBody(MenuWidget())
    offcanvasWidget.title = "Select one menu option"
    bodyHolder.show(bootrapWidget)
    container.append(offcanvasWidget.container)
    val holder = bootrapWidget.mainHolder
    holder.show(mainWidget)
    holder.show(loginWidget)
    bootrapWidget.onHamburgerClick = { offcanvasWidget.toggle() }
    HotkeyWindow
        .add("SHIFT-F3") { holder.show(HtmlSignalWidget.shared) }
        .add("F8") { launchJs { ApiTmEventRequest(1234, "Esc was pressed").send() } }
        .add("F1") { holder.show(loginWidget) }
        .add("F2") { holder.show(mainWidget) }
        .add("F3") { holder.show(SearchWidget()) }
        .add("F4") {
            ToastWidget().apply {
                body = "ciaoo"
                show()
            }
        }
}
