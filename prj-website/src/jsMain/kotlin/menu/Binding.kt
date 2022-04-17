package menu

import state.JsState

fun JsState.menuBindings(): Map<Menu, () -> Unit> = buildMap {
    val map = this
    infix fun Menu.bindTo(func: () -> Unit) = run { map[this] = func }
    root.apply {
        logoff bindTo { logoffController() }
    }
}

fun JsState.logoffController() {
    toast("Logoff request")
}