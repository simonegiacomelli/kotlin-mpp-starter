import api.names.ApiTmEventRequest
import coroutine.WaitContinuation
import forms.login.LoginWidget
import keyboard.HotkeyWindow
import kotlinx.datetime.Clock
import pages.bootstrap.MainWidget
import pages.bootstrap.MenuWidget
import pages.bootstrap.SearchWidget
import pages.bootstrap.ToastWidget
import pages.forms.HtmlSignalWidget
import rpc.send
import state.JsState
import state.installClientHandler
import utils.launchJs

const val version = "v0.1.4"

suspend fun main() {
    println("ok $version " + (Clock.System.now()))
    installClientHandler().login()
    OnewayApi.openWebSocket()
}

private suspend fun JsState.login() = widgets.apply {

    body.append(holder.container)
    HotkeyWindow.log_prefix = "HotkeyWindow"
    WaitContinuation<Unit>("wait login").apply {
        runWaitResume { holder.show(LoginWidget { resume(Unit) }) }
    }

    val mainWidget = MainWidget()
    offcanvas.setBody(MenuWidget())
    offcanvas.title = "Select one menu option"
    holder.show(navbar)
    body.append(offcanvas.container)
    val holder = navbar.mainHolder
    holder.show(mainWidget)
    navbar.onHamburgerClick = { offcanvas.toggle() }
    HotkeyWindow
        .add("SHIFT-F3") { holder.show(HtmlSignalWidget.shared) }
        .add("F8") { launchJs { ApiTmEventRequest(1234, "Esc was pressed").send() } }
        .add("F2") { holder.show(mainWidget) }
        .add("F3") { holder.show(SearchWidget()) }
        .add("F4") {
            ToastWidget().apply {
                body = "ciaoo"
                show()
            }
        }
}
