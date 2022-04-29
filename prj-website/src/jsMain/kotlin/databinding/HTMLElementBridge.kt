package databinding

import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

fun HTMLElementBridge(target: HTMLElement) =
    if (target is HTMLInputElement) object : TargetProperty<String> {
        override var value: String
            get() = target.value
            set(value) {
                target.value = value
            }
    } else object : TargetProperty<String> {
        override var value: String
            get() = target.innerHTML
            set(value) {
                target.innerHTML = value
            }
    }