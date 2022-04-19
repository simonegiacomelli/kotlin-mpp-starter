package databinding

import org.w3c.dom.HTMLInputElement
import pages.bootstrap.databinding.HtmlInputTarget
import kotlin.reflect.KMutableProperty0

fun intBridge(target: HTMLInputElement): KMutableProperty0<Int> = IntBridge(target)::value
fun intTarget(target: HTMLInputElement) = HtmlInputTarget(target, intBridge(target))

private class IntBridge(val target: HTMLInputElement) {
    var value: Int
        get() = target.value.toIntOrNull() ?: -1
        set(value) = run { target.value = "$value" }
}
