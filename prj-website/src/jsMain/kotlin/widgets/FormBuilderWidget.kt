package widgets

import databinding.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import utils.forward
import widget.Widget
import kotlin.reflect.KMutableProperty1

// https://getbootstrap.com/docs/5.0/forms/layout/#forms
class FormBuilderWidget : Widget(//language=HTML
    """<form id="idForm"></form>"""
) {
    private val idForm: HTMLElement by this

    fun <S> bind(sourceInstance: S, sourceProperty: KMutableProperty1<S, Boolean>): BindableBooleanWidget {
        val w = BindableBooleanWidget()
        bind(sourceInstance, sourceProperty, BooleanBridgeNN(w.inputElement))
        idForm.append(w.container)
        w.label = sourceProperty.name.capitalize()
        return w
    }

    fun <S> bind(sourceInstance: S, sourceProperty: KMutableProperty1<S, Int>): BindableStringWidget {
        val w = BindableStringWidget()
        bind(sourceInstance, sourceProperty, IntBridge(w.inputElement))
        idForm.append(w.container)
        w.label = sourceProperty.name.capitalize()
        return w
    }

    fun <S> bind(sourceInstance: S, sourceProperty: KMutableProperty1<S, String>): BindableStringWidget {
        val w = BindableStringWidget()
        bind(sourceInstance, sourceProperty, StringBridge(w.inputElement))
        idForm.append(w.container)
        w.label = sourceProperty.name.capitalize()
        return w
    }

    fun <S> bind(sourceInstance: S, sourceProperty: KMutableProperty1<S, String?>): BindableStringWidget {
        val w = BindableStringWidget()
        bind(sourceInstance, sourceProperty, StringBridgeN(w.inputElement))
        idForm.append(w.container)
        w.label = sourceProperty.name.capitalize()
        return w
    }
}

class BindableBooleanWidget : Widget(//language=HTML
    """<fieldset class="row mb-3">
    <legend class="col-form-label col-sm-2 pt-0" id='labelElement'></legend>
    <div class="col-sm-10">
        <div class="form-check form-switch">
            <input class="form-check-input" type="checkbox" id="inputElement">
        </div>
    </div>
</fieldset>
"""
) {
    val inputElement: HTMLInputElement by this
    val labelElement: HTMLElement by this
    var label: String by forward { labelElement::innerHTML }
}

class BindableStringWidget : Widget(//language=HTML
    """ <div class="row mb-3">
    <label for="inputElement" class="col-sm-2 col-form-label pt-0" id='labelElement'></label>
    <div class="col-sm-10">
      <input type="text" class="form-control" id="inputElement">
    </div>
  </div> 
"""
) {
    val inputElement: HTMLInputElement by this
    val labelElement: HTMLElement by this
    var label: String by forward { labelElement::innerHTML }
}