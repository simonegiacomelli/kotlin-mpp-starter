package databinding

import org.w3c.dom.HTMLElement
import kotlin.reflect.KProperty

interface HtmlInputChangesNotifier : ChangesNotifier {
    val target: HTMLElement

    override fun addChangeListener(listener: (property: KProperty<*>) -> Unit) {
        target.addEventListener("input", { listener(target::innerHTML) })
    }
}
