package forms.settings

import databinding.Bindable
import grid.GridWidget
import widget.containerElement


val UiSettings = UiSettingsClass()

class UiSettingsClass : Bindable() {
    var gridTableResponsive: Boolean by this(true)

    fun install() {
        GridWidget.defaultInit = {
            console.log("gridTableResponsive=$gridTableResponsive")
            if (gridTableResponsive)
                it.containerElement.classList.add("table-responsive")
            else
                it.containerElement.classList.remove("table-responsive")
        }

    }
}