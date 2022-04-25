package widgets

import databinding.BooleanBridgeNN
import databinding.bind
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import widget.Widget
import kotlin.reflect.KMutableProperty1

// https://getbootstrap.com/docs/5.0/forms/layout/#forms
class FormBuilderWidget : Widget(//language=HTML
    """
    
"""
) {
    init {
        explicitContainer = document.createElement("form")
    }

    fun <S> add(sourceInstance: S, sourceProperty: KMutableProperty1<S, Boolean>) {
        val w = BindableBooleanWidget()
        bind(sourceInstance, sourceProperty, BooleanBridgeNN(w.flexSwitchCheckDefault))
        container.append(w.container)
    }
}

private class BindableBooleanWidget : Widget(//language=HTML
    """<div class="row mb-3">
    <label for="input1234" class="col-sm-2 col-form-label">label-todo</label>
    <div class="col-sm-10">
        <div class="form-check form-switch">
            <input class="form-check-input" type="checkbox" id="flexSwitchCheckDefault">
        </div>
    </div>
</div>    
"""
) {
    val flexSwitchCheckDefault: HTMLInputElement by this
}