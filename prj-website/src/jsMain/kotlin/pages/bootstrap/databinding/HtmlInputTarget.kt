package pages.bootstrap.databinding

import databinding.Target
import org.w3c.dom.HTMLInputElement
import kotlin.reflect.KMutableProperty0

class HtmlInputTarget<T>(
    val target: HTMLInputElement,
    override val bridge: KMutableProperty0<T>
) : Target<T> {

    override fun onChange(listener: () -> Unit) {
        target.addEventListener("input", { listener() })
    }
}