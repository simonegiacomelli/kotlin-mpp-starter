package pages.bootstrap.databinding

import databinding.InternallyChangeable
import databinding.Target
import org.w3c.dom.HTMLInputElement
import utils.forward
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

class HtmlInputTarget<T>(
    private val inputElement: HTMLInputElement,
    override val property: KMutableProperty0<T>
) : Target<T>, InternallyChangeable {
    var value by forward { inputElement::value }
    override fun onChange(listener: (property: KProperty<*>) -> Unit) {
        inputElement.addEventListener("input", { listener(this.property) })
    }
}


