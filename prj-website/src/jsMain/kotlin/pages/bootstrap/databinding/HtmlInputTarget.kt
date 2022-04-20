package pages.bootstrap.databinding

import databinding.Target
import org.w3c.dom.HTMLInputElement
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

class HtmlInputTarget<T>(
    private val inputElement: HTMLInputElement,
    override val property: KMutableProperty0<T>
) : Target<T> {

    override fun onChange(listener: (property: KProperty<*>) -> Unit): (KProperty<*>) -> Unit {
        val callback = { property: KProperty<*> -> listener(this.property) }
        inputElement.addEventListener("input", { callback(property) })
        return callback
    }
}