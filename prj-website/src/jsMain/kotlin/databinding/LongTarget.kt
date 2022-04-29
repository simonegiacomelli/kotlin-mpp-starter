package databinding

import org.w3c.dom.HTMLElement

class LongTarget(override val target: HTMLElement) : TargetProperty<Long>, HtmlElementObservable {
    private val pb = HTMLElementBridge(target)

    override var value: Long
        get() = pb.value.toLongOrNull() ?: -1
        set(value) = run { pb.value = "$value" }
}


