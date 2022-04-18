package pages.bootstrap.databinding

import org.w3c.dom.HTMLInputElement
import kotlin.reflect.KMutableProperty0

class HtmlInputTarget<T>(
    val target: HTMLInputElement,
    override val bridge: KMutableProperty0<T>
) : Target<T> {

    override fun notifyOnChange(listener: () -> Unit) {
        target.addEventListener("input", { listener() })
    }

//    override val property: KMutableProperty1<HTMLInputElement, String> = HTMLInputElement::value
}