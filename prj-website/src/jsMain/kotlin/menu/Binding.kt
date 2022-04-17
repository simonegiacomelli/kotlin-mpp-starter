package menu

import api.names.ApiAcLogoffRequest
import kotlinx.browser.window
import kotlinx.coroutines.withTimeout
import removeAppComponents
import rpc.send
import startupApplication
import state.JsState
import utils.launchJs

fun JsState.menuBindings(): Map<Menu, () -> Unit> = buildMap {
    val map = this
    infix fun Menu.bindTo(func: () -> Unit) = run { map[this] = func }
    root.apply {
        logoff bindTo { logoffController() }
    }
}

fun JsState.logoffController() = spinner {
    runCatching { withTimeout(1000) { session_id?.also { ApiAcLogoffRequest(it).send() } } }
    sessionOrNull = null
    removeAppComponents()
    window.setTimeout({ launchJs { startupApplication() } }, 1)
}