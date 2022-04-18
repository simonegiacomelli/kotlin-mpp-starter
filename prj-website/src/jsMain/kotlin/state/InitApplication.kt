package state

import api.names.ApiTmEventRequest
import coroutine.WaitContinuation
import forms.login.LoginWidget
import keyboard.HotkeyWindow
import kotlinx.dom.clear
import menu.Menu
import menu.acceptedSet
import menu.menuBindings
import menu.root
import pages.LoaderWidget
import pages.bootstrap.CalculatorWidget
import pages.bootstrap.SearchWidget
import pages.bootstrap.ToastWidget
import pages.forms.HtmlDisplayWidget
import rpc.send

suspend fun startupApplication() {
    val jsState = JsState()
    stateOrNull = { jsState }
    jsState.addLoginComponents()
}

private suspend fun JsState.addLoginComponents() = widgets.apply {
    body.clear()
    body.append(rootHolder.container)
    body.append(LoaderWidget.shared.container)
    body.append(offcanvas.container)
    body.append(toastStack.container)

    HotkeyWindow.log_prefix = "HotkeyWindow"

    WaitContinuation<Unit>("wait login").apply {
        runWaitResume { rootHolder.show(LoginWidget { resume(Unit) }) }
    }

    fun noMenuBinding(menu: Menu) = run { if (menu.parent != root) toast("No binding for menu: ${menu.name}") }

    val menuBindings = menuBindings()
    fun menuClick(menu: Menu) {
        val function = menuBindings[menu] ?: return noMenuBinding(menu)
        offcanvas.close()
        function()
    }

    menu.onElementClick = { menuClick(it) }
    menu.onCaption = { it.caption }
    val childrenMap = root.acceptedSet(user.roles).groupBy { it.parent }
    menu.onGetChildren = { childrenMap[it ?: root] ?: emptyList() }
    menu.render()

    val mainWidget = CalculatorWidget()
    offcanvas.setBody(menu)
    offcanvas.title = "Select one menu option"
    rootHolder.show(navbar)

    holder.show(mainWidget)
    navbar.onHamburgerClick = { offcanvas.toggle() }
    HotkeyWindow
        .add("SHIFT-F3") { holder.show(HtmlDisplayWidget.shared) }
        .add("F8") { coroutine.launch { ApiTmEventRequest(1234, "Esc was pressed").send() } }
        .add("F2") { holder.show(mainWidget) }
        .add("F3") { holder.show(SearchWidget()) }
        .add("F4") {
            ToastWidget().apply {
                body = "ciaoo"
                show()
            }
        }
}
