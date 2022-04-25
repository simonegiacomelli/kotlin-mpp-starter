package state

import api.names.ApiAcVerifySessionRequest
import api.names.ApiTmNewEventRequest
import controller.login.sessionOk
import coroutine.WaitContinuation
import forms.login.LoginWidget
import forms.login.SessionCheckWidget
import keyboard.HotkeyWindow
import kotlinx.browser.document
import kotlinx.dom.clear
import menu.Menu
import menu.RootMenu
import menu.acceptedSet
import menu.menuBindings
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
    body.append(offcanvas.container)
    body.append(spinner.container)
    body.append(toastStack.container)

    HotkeyWindow.log_prefix = "HotkeyWindow"

    authenticate()

    fun noMenuBinding(menu: Menu) = run { if (menu.parent != RootMenu) toast("No binding for menu: ${menu.name}") }

    val menuBindings = menuBindings()
    fun menuClick(menu: Menu) {
        val function = menuBindings[menu] ?: return noMenuBinding(menu)
        offcanvas.close()
        if (menu != RootMenu.logoff) document.location?.apply { hash = "#${menu.name}" }
        function()
    }

    menu.onElementClick = { menuClick(it) }
    menu.onCaption = { it.caption }
    val availableMenu = RootMenu.acceptedSet(user.roles)
    val childrenMap = availableMenu.groupBy { it.parent }
    menu.onGetChildren = { childrenMap[it ?: RootMenu] ?: emptyList() }
    menu.onCellRender = { if (depth > 0) cell.classList.add("py-0") }
    menu.render()

    offcanvas.setBody(menu)
    offcanvas.title = "Menu"
    rootHolder.show(navbar)

    navbar.onHamburgerClick = { offcanvas.toggle() }
    HotkeyWindow
        .add("SHIFT-F3") { holder.show(HtmlDisplayWidget.shared) }
        .add("F8") { coroutine.launch { ApiTmNewEventRequest(1234, "Esc was pressed").send() } }
        .add("F3") { holder.show(SearchWidget()) }
        .add("F4") {
            ToastWidget().apply {
                body = "ciaoo"
                show()
            }
        }
    holder.show(CalculatorWidget())

    val menuNameMap = availableMenu.associateBy { it.name }
    document.location?.run {
        val menuName = hash.removePrefix("#")
        if (menuName.isNotBlank())
            menuNameMap.getOrElse(menuName) { toast("Menu `$menuName` not found"); null }
                ?.also { menuClick(it) }
    }

}

private suspend fun JsState.authenticate(): Unit = widgets.run {

    val sessionId = session_id
    if (sessionId != null) {
        rootHolder.show(SessionCheckWidget())
        val session = spinnerSuspend { ApiAcVerifySessionRequest(sessionId).send().session }
        if (session != null)
            return@run sessionOk(session)
        else
            sessionOrNull = null // remove invalid sessionId
    }


    WaitContinuation<Unit>("wait login").apply {
        runWaitResume { rootHolder.show(LoginWidget { resume(Unit) }) }
    }

}
