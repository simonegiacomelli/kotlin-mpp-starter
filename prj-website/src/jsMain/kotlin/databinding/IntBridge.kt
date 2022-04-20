package databinding

import org.w3c.dom.HTMLInputElement

class IntBridge(override val target: HTMLInputElement) : PropertyBridge<Int>, HtmlInputChangesNotifier {
    override var value: Int
        get() = target.value.toIntOrNull() ?: -1
        set(value) = run { target.value = "$value" }
}
