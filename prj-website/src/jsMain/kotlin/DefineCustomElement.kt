import extensions.div
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.OPEN
import org.w3c.dom.ShadowRootInit
import org.w3c.dom.ShadowRootMode
import kotlin.reflect.KProperty

fun defineCustomElementTest() {
    defineCustomElement(MyElement2)
    document.body?.append(document.createElement("my-element2"))
    document.body?.append(MyElement2.createElement())
    document.body?.append(div("ciao!") { id = "div1" })
    val div2 = div("ciao!") { id = "div2" }
    document.body?.append(div2)
    div2.innerHTML = """<my-element2 id="me2" size="3" color="red">heeeeyyy</my-element2>"""

    console.log("defineCustomElemenTest")
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

class MyElement2 : AbsCustomElement() {

    companion object : CEMeta<MyElement2> by ceMeta("my-element2", ::MyElement2, arrayOf("size", "color"))

    var size by attributes

    override fun constr() {
        console.log("constructor MyElement2 - kotlin")
        element.attachShadow(ShadowRootInit(ShadowRootMode.OPEN))
        updateText()
    }

    private fun updateText() {
        val size = size?.toIntOrNull() ?: 1
        val s = size.coerceAtMost(10).coerceAtLeast(1)
        element.shadowRoot.asDynamic().innerHTML = "<h$s>Hello World2</h$s>"
    }

    override fun connectedCallback() {
        console.log("connectedCallback")
    }

    override fun disconnectedCallback() {
        console.log("disconnectedCallback")
    }

    override fun adoptedCallback() {
        console.log("adoptedCallback")
    }

    override fun attributeChangedCallback(name: String, oldValue: String, newValue: String) {
        console.log("attributeChangedCallback", name, oldValue, newValue)
        console.log("size", if (size == null) "<null>" else size)
        updateText()
    }
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