package databinding

import org.w3c.dom.HTMLInputElement

class BooleanTarget(override val target: HTMLInputElement) : TargetProperty<Boolean>, HtmlElementObservable {
    override var value by target::checked
}


