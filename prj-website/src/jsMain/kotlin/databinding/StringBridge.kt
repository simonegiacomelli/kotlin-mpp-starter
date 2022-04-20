package databinding

import org.w3c.dom.HTMLInputElement
import pages.bootstrap.databinding.HtmlInputTarget
import kotlin.reflect.KMutableProperty0

fun stringBridge(target: HTMLInputElement): KMutableProperty0<String> = StringBridge(target)::value
fun stringTarget(target: HTMLInputElement) = HtmlInputTarget(target, stringBridge(target))

class StringBridge(override val target: HTMLInputElement) : PropertyBridge<String>, HtmlInputTarget2<String> {
    override var value: String
        get() = target.value
        set(value) = run { target.value = value }

    override val property: KMutableProperty0<String> get() = this::value
}


