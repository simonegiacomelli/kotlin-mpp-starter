package menu

import api.names.ApiAcLogoffRequest
import kotlinx.browser.window
import kotlinx.coroutines.withTimeout
import pages.bootstrap.CalculatorWidget
import pages.bootstrap.UserChangeWidget
import pages.bootstrap.UserCreateWidget
import pages.bootstrap.UserPasswdWidget
import pages.forms.HtmlDisplayWidget
import pages.forms.HtmlEditorWidget
import rpc.send
import state.JsState
import state.startupApplication
import widget.Widget

fun JsState.menuBindings(): Map<Menu, () -> Unit> = buildMap {
    fun show(widget: Widget) = widgets.holder.show(widget)
    val map = this
    infix fun Menu.bindTo(func: () -> Unit) = run { map[this] = func }
    root.apply {
        accessControl.apply {
            userChange bindTo { show(UserChangeWidget()) }
            userCreate bindTo { show(UserCreateWidget()) }
            userPasswd bindTo { show(UserPasswdWidget()) }
        }
        math.apply {
            calculator bindTo { show(CalculatorWidget()) }
        }
        development.apply {
            html_editor bindTo { show(HtmlEditorWidget()) }
            html_display bindTo { show(HtmlDisplayWidget.shared) }
        }
        logoff bindTo { logoffController() }

    }
}

fun JsState.logoffController() = spinner {
    runCatching { withTimeout(1000) { session_id?.also { ApiAcLogoffRequest(it).send() } } }
    sessionOrNull = null
    window.setTimeout({ coroutine.launch { startupApplication() } }, 1)
}