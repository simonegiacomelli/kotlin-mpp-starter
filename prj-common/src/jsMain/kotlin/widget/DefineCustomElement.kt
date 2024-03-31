package widget

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import kotlin.reflect.KProperty

@JsExport
open class AbsCustomElement : AbsCe() {

    var _element: HTMLElement? = null
    override val element: HTMLElement get() = _element ?: error("element not set")


    open fun constr() {}

    open fun connectedCallback() {}

    open fun disconnectedCallback() {}

    open fun adoptedCallback() {}

    open fun attributeChangedCallback(name: String, oldValue: String, newValue: String) {}
}

interface CEMeta<T : AbsCustomElement> {
    val tagName: String
    val className: String
    val kotlinConstructor: () -> T
    val observedAttributes: Array<String>
    fun createElement(): HTMLElement
}

inline fun <reified T : AbsCustomElement> ceMeta(
    tagName: String,
    noinline kotlinConstructor: () -> T,
    observedAttributes: Array<String> = emptyArray()
): CEMeta<T> = object : CEMeta<T> {
    override val tagName: String = tagName
    override val kotlinConstructor: () -> T = kotlinConstructor
    override val observedAttributes: Array<String> get() = observedAttributes
    override val className: String = T::class.simpleName ?: error("simpleName not found")
    override fun createElement(): HTMLElement = document.createElement(tagName) as HTMLElement
}

fun defineCustomElement(cem: CEMeta<*>) {
    val className = cem.className
    window.asDynamic()["kotlin_constructor_$className"] = cem.kotlinConstructor
    val code = _customElement
        .replace("#ClassName", className)
        .replace("#tagName", cem.tagName)
        .replace("#observedAttributes", cem.observedAttributes.joinToString { "\"$it\"" })
    eval(code)
}

//language=JavaScript
val _customElement = """
class #ClassName extends HTMLElement {
    static observedAttributes = [ #observedAttributes ];
    
    constructor() {
        super();
        this._kt = window.kotlin_constructor_#ClassName();
        this._kt._element = this;
        this._kt.constr();
    }

    connectedCallback() { this._kt.connectedCallback(); }
    disconnectedCallback() { this._kt.disconnectedCallback(); }
    adoptedCallback() { this._kt.adoptedCallback(); }
    attributeChangedCallback(name, oldValue, newValue) { this._kt.attributeChangedCallback(name, oldValue, newValue); }
}

customElements.define('#tagName', #ClassName);
window.#ClassName = #ClassName;
"""

abstract class AbsCe {
    abstract val element: HTMLElement

    val attributes = Attributes()

    inner class Attributes {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String? =
            if (element.hasAttribute(property.name))
                element.getAttribute(property.name)
            else
                null

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) =
            if (value != null)
                element.setAttribute(property.name, value)
            else
                element.removeAttribute(property.name)
    }

}
