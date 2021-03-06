package databinding

import org.w3c.dom.HTMLElement
import kotlin.reflect.KProperty

interface HtmlElementObservable : Observable {
    val target: HTMLElement

    override fun addObserver(observer: (property: KProperty<*>) -> Unit) {
        target.addEventListener("input", { observer(target::innerHTML) })
    }
}
