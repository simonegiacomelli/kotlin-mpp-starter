package forms.settings

import databinding.BooleanBridgeNN
import databinding.bind
import org.w3c.dom.HTMLInputElement
import widget.Widget

class SettingsWidget : Widget(//language=HTML
    """
<h5>settings</h5>   
 
 <div class="form-check form-switch">
  <input class="form-check-input" type="checkbox" id="flexSwitchCheckDefault">
  <label class="form-check-label" for="flexSwitchCheckDefault">responsive GridWidget</label>
</div>
"""
) {
    private val flexSwitchCheckDefault: HTMLInputElement by this
    override fun afterRender() {
        bind(UiSettings, UiSettingsClass::gridTableResponsive, BooleanBridgeNN(flexSwitchCheckDefault))
    }


}
