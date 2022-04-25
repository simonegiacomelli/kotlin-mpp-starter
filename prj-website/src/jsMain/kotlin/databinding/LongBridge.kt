package databinding

import org.w3c.dom.HTMLElement

class LongBridge(override val target: HTMLElement) : PropertyBridge<Long>, HtmlInputChangesNotifier {
    private val pb = HTMLElementBridge(target)

    override var value: Long
        get() = pb.value.toLongOrNull() ?: -1
        set(value) = run { pb.value = "$value" }
}


