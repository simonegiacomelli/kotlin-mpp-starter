package forms.settings

import grid.GridWidget
import widget.containerElement


val UiSettings = UiSettingsClass()

class UiSettingsClass {
    var gridTableResponsive: Boolean = true

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