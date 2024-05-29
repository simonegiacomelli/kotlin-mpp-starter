package widget

import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import kotlin.reflect.KProperty

const val namespace_kotlin_constructors = "window.kotlin_custom_elements.kotlin_constructors"
const val namespace_javascript_constructors = "window.kotlin_custom_elements.javascript_constructors"

@JsExport
abstract class AbsCustomElement : AbsCe() {

    var _element: HTMLElement? = null

    override val element: HTMLElement
        get() {
            if (_element == null) createElement()
            return _element ?: error("element not set")
        }

    abstract fun createElement(): HTMLElement

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

    fun createElement(): HTMLElement = document.createElement(tagName) as HTMLElement
    fun createElementKotlinSide(ktInstance: T): HTMLElement =
        eval("new $namespace_javascript_constructors.$className(ktInstance)") as HTMLElement

    fun register() = run { defineCustomElement(this) }
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
}

fun defineCustomElement(cem: CEMeta<*>) {
    listOf(namespace_kotlin_constructors, namespace_javascript_constructors)
        .forEach { ns ->
            eval(
                // language=JavaScript
                """
                let inputString = '$ns';
                let parts = inputString.split('.');
                let current = window;
                
                for (let part of parts) {
                    if (!current[part]) current[part] = {};
                    current = current[part];
                }
            """.trimIndent()
            )
        }
    eval(namespace_kotlin_constructors)[cem.className] = cem.kotlinConstructor
    val code = _customElement
        .replace("#ClassName", cem.className)
        .replace("#tagName", cem.tagName)
        .replace("#observedAttributes", cem.observedAttributes.joinToString { "\"$it\"" })
    eval(code)
}

//language=JavaScript
val _customElement = """
class #ClassName extends HTMLElement {
    static observedAttributes = [ #observedAttributes ];
    
    constructor(kotlin_instance) {
        super();
        if(kotlin_instance) 
            this._kt = kotlin_instance;
        else
            this._kt = $namespace_kotlin_constructors.#ClassName();
            
        this._kt._element = this;
        this._kt.constr();
    }

    connectedCallback() { this._kt.connectedCallback(); }
    disconnectedCallback() { this._kt.disconnectedCallback(); }
    adoptedCallback() { this._kt.adoptedCallback(); }
    attributeChangedCallback(name, oldValue, newValue) { this._kt.attributeChangedCallback(name, oldValue, newValue); }
}

customElements.define('#tagName', #ClassName);
$namespace_javascript_constructors.#ClassName = #ClassName;
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
