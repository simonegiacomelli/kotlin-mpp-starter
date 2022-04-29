package databinding

import org.w3c.dom.HTMLElement

class IntTarget(override val target: HTMLElement) : TargetProperty<Int>, HtmlElementObservable {
    private val pb = HTMLElementBridge(target)

    override var value: Int
        get() = pb.value.toIntOrNull() ?: -1
        set(value) = run { pb.value = "$value" }
}


