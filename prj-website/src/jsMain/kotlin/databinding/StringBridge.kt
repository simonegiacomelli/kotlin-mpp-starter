package databinding

import org.w3c.dom.HTMLInputElement


class StringBridge(override val target: HTMLInputElement) : PropertyBridge<String>, HtmlInputTarget {
    override var value: String
        get() = target.value
        set(value) = run { target.value = value }
}


