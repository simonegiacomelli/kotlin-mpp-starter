package pages.bootstrap.databinding

import org.w3c.dom.HTMLInputElement
import kotlin.reflect.KMutableProperty0

fun stringBridge(target: HTMLInputElement): KMutableProperty0<String> = StringBridge(target)::value
fun stringTarget(target: HTMLInputElement) = HtmlInputTarget(target, stringBridge(target))

private class StringBridge(val target: HTMLInputElement) {
    var value: String
        get() = target.value
        set(value) = run { target.value = value }
}
