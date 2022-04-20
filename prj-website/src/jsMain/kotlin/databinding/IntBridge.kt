package databinding

import org.w3c.dom.HTMLInputElement
import kotlin.reflect.KMutableProperty0

class IntBridge(override val target: HTMLInputElement) : PropertyBridge<Int>, HtmlInputTarget<Int> {
    override var value: Int
        get() = target.value.toIntOrNull() ?: -1
        set(value) = run { target.value = "$value" }

    override val property: KMutableProperty0<Int> get() = this::value
}
