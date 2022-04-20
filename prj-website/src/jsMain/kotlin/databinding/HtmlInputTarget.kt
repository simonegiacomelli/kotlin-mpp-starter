package databinding

import org.w3c.dom.HTMLInputElement
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

interface HtmlInputTarget<T> : InternallyChangeable {
    val target: HTMLInputElement
    val property: KMutableProperty0<T>

    override fun onChange(listener: (property: KProperty<*>) -> Unit) {
        target.addEventListener("input", { listener(this.property) })
    }
}
