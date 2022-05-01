package widgets

import databinding.*
import kotlinx.datetime.LocalDateTime
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

    fun <S> bind(sourceInstance: S, sourceProperty: KMutableProperty1<S, Boolean>): WithLabel =
        BindableBooleanWidget().bindTo(sourceInstance, sourceProperty) { BooleanTarget(it) }

    fun <S> bind(sourceInstance: S, sourceProperty: KMutableProperty1<S, Int>): WithLabel =
        BindableStringWidget().bindTo(sourceInstance, sourceProperty) { IntTarget(it) }


    fun <S> bind(sourceInstance: S, sourceProperty: KMutableProperty1<S, String>): WithLabel =
        BindableStringWidget().bindTo(sourceInstance, sourceProperty) { StringTarget(it) }


    fun <S> bind(sourceInstance: S, sourceProperty: KMutableProperty1<S, String?>): WithLabel =
        BindableStringWidget().bindTo(sourceInstance, sourceProperty) { StringTargetN(it) }


    fun <S> bind(sourceInstance: S, sourceProperty: KMutableProperty1<S, LocalDateTime?>): WithLabel =
        BindableStringWidget().bindTo(sourceInstance, sourceProperty) { LocalDateTimeTargetN(it) }


    private fun <T, S, W> W.bindTo(
        sourceInstance: S,
        sourceProperty: KMutableProperty1<S, T>,
        target: (HTMLInputElement) -> TargetProperty<T>
    ): W where W : Widget, W : WithLabel, W : WithInput {
        bind(sourceInstance, sourceProperty, target(inputElement))
        idForm.append(container)
        label = sourceProperty.name.capitalize()
        return this
    }
}

interface WithLabel {
    var label: String
}

interface WithInput {
    val inputElement: HTMLInputElement
}

private class BindableBooleanWidget : WithLabel, WithInput, Widget(//language=HTML
    """
<fieldset class="row mb-3">
    <legend class="col-form-label col-sm-2 pt-0" id='labelElement'></legend>
    <div class="col-sm-10">
        <div class="form-check form-switch">
            <input class="form-check-input" type="checkbox" id="inputElement">
        </div>
    </div>
</fieldset>
"""
) {
    override val inputElement: HTMLInputElement by this
    val labelElement: HTMLElement by this
    override var label: String by forward { labelElement::innerHTML }
}

private class BindableStringWidget : WithLabel, WithInput, Widget(//language=HTML
    """
<div class="row mb-3">
    <label for="inputElement" class="col-sm-2 col-form-label pt-0" id='labelElement'></label>
    <div class="col-sm-10">
      <input type="text" class="form-control" id="inputElement">
    </div>
</div> 
"""
) {
    override val inputElement: HTMLInputElement by this
    val labelElement: HTMLElement by this
    override var label: String by forward { labelElement::innerHTML }
}