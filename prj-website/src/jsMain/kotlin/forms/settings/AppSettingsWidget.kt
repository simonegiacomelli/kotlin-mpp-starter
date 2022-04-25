package forms.settings

import widget.Widget
import widgets.FormBuilderWidget

class AppSettingsWidget : Widget(//language=HTML
    """
<h5>settings</h5>   

"""
) {
    override fun afterRender() {
        FormBuilderWidget().also {
            it.add(UiSettings, UiSettingsClass::gridTableResponsive)
            container.append(it.container)
        }
    }


}
