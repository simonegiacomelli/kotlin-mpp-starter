package databinding

import org.w3c.dom.HTMLInputElement
import kotlin.reflect.KMutableProperty0


class StringBridge(override val target: HTMLInputElement) : PropertyBridge<String>, HtmlInputTarget<String> {
    override var value: String
        get() = target.value
        set(value) = run { target.value = value }

    override val property: KMutableProperty0<String> get() = this::value
}


