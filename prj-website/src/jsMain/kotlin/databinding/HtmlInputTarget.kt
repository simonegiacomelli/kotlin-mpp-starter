package databinding

import org.w3c.dom.HTMLInputElement
import kotlin.reflect.KProperty

interface HtmlInputTarget : InternallyChangeable {
    val target: HTMLInputElement

    override fun onChange(listener: (property: KProperty<*>) -> Unit) {
        target.addEventListener("input", { listener(target::value) })
    }
}
