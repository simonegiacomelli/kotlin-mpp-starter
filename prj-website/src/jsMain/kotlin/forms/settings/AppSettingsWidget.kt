package forms.settings

import widget.Widget
import widgets.FormBuilderWidget

class AppSettingsWidget : Widget(//language=HTML
    """
<h5>settings</h5>   
<div id='divForm'></div>
"""
) {
    private val divForm by this { FormBuilderWidget() }
    override fun afterRender() {
        divForm.apply {
            bind(UiSettings, UiSettingsClass::gridTableResponsive).label = "Responsive tables"
        }

    }


}
