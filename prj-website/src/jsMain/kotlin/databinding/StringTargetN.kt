package databinding

import org.w3c.dom.HTMLElement

class StringTargetN(override val target: HTMLElement) : TargetProperty<String?>, HtmlElementObservable {
    private val pb = HTMLElementBridge(target)
    override var value: String?
        get() = pb.value.ifEmpty { null }
        set(value) = run { pb.value = value ?: "" }
}