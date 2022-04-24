package menu

import kotlinx.coroutines.delay
import pages.bootstrap.CalculatorWidget
import pages.bootstrap.UserChangeWidget
import pages.bootstrap.UserCreateWidget
import pages.bootstrap.UserPasswdWidget
import pages.bootstrap.databinding.demo.DataBindingDemoWidget
import pages.bootstrap.databinding.demo.TwoWayBindingDemoWidget
import pages.bootstrap.dateinput.DateInputDelphiStyleWidget
import pages.forms.HtmlDisplayWidget
import pages.forms.HtmlEditorWidget
import state.JsState
import state.logoffApplication
import widget.Widget

fun JsState.menuBindings(): Map<Menu, () -> Unit> = buildMap {
    fun show(widget: Widget) = widgets.holder.show(widget)
    val map = this
    infix fun Menu.onClick(func: () -> Unit) =
        run { if (map[this] != null) error("menu $name already bound"); map[this] = func }
    menuRoot.apply {
        accessControl.apply {
            userChange onClick { show(UserChangeWidget()) }
            userCreate onClick { show(UserCreateWidget()) }
            userPasswd onClick { show(UserPasswdWidget()) }
        }
        math.apply {
            calculator onClick { show(CalculatorWidget()) }
        }
        development.apply {
            date_input_delphi_style onClick { show(DateInputDelphiStyleWidget()) }
            spinner onClick { spinner { delay(3000) } }
            html_editor onClick { show(HtmlEditorWidget()) }
            html_display onClick { show(HtmlDisplayWidget.shared) }
            two_way_data_binding onClick { show(TwoWayBindingDemoWidget()) }
            data_binding_demo onClick { show(DataBindingDemoWidget()) }
        }
        logoff onClick { logoffApplication() }

    }
}
