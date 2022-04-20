package databinding

import org.w3c.dom.HTMLInputElement
import kotlin.reflect.KProperty

interface HtmlInputChangesNotifier : ChangesNotifier {
    val target: HTMLInputElement

    override fun addChangeListener(listener: (property: KProperty<*>) -> Unit) {
        target.addEventListener("input", { listener(target::value) })
    }
}
