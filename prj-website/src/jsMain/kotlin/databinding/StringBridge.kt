package databinding

import org.w3c.dom.HTMLElement


class StringBridge(override val target: HTMLElement) : PropertyBridge<String>, HtmlElementObservable {
    private val pb = HTMLElementBridge(target)
    override var value: String
        get() = pb.value
        set(value) = run { pb.value = value }
}

class StringBridgeN(override val target: HTMLElement) : PropertyBridge<String?>, HtmlElementObservable {
    private val pb = HTMLElementBridge(target)
    private var isNull: Boolean = true
    override var value: String?
        get() = if (isNull) null else pb.value
        set(value) = run { pb.value = value ?: ""; isNull = value == null; }
}


