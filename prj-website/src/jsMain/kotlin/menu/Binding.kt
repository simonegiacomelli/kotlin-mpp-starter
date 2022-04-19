package menu

import kotlinx.coroutines.delay
import pages.bootstrap.CalculatorWidget
import pages.bootstrap.UserChangeWidget
import pages.bootstrap.UserCreateWidget
import pages.bootstrap.UserPasswdWidget
import pages.bootstrap.databinding.demo.DataBindingDemoWidget
import pages.bootstrap.databinding.demo.DataBindingWidget
import pages.forms.HtmlDisplayWidget
import pages.forms.HtmlEditorWidget
import state.JsState
import state.logoffApplication
import widget.Widget

fun JsState.menuBindings(): Map<Menu, () -> Unit> = buildMap {
    fun show(widget: Widget) = widgets.holder.show(widget)
    val map = this
    infix fun Menu.bindTo(func: () -> Unit) =
        run { if (map[this] != null) error("menu $name already bound"); map[this] = func }
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
            spinner bindTo { spinner { delay(3000) } }
            html_editor bindTo { show(HtmlEditorWidget()) }
            html_display bindTo { show(HtmlDisplayWidget.shared) }
            two_way_data_binding bindTo { show(DataBindingWidget()) }
            data_binding_demo bindTo { show(DataBindingDemoWidget()) }
        }
        logoff bindTo { logoffApplication() }

    }
}
