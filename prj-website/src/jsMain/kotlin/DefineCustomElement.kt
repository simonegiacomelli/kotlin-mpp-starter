import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.OPEN
import org.w3c.dom.ShadowRootInit
import org.w3c.dom.ShadowRootMode

fun defineCustomElementTest() {
    defineCustomElement(MyElement2)
    document.body?.append(document.createElement("my-element2"))
    document.body?.append(MyElement2.factory.createElement())
    console.log("defineCustomElemenTest")
}

interface CustomElementFactory<T : AbsCustomElement> {
    val tagName: String
    val className: String
    val create: () -> T
    val observedAttributes: Array<String>
    fun createElement(): HTMLElement
}

inline fun <reified T : AbsCustomElement> ceFactory(
    tagName: String,
    noinline factory: () -> T,
    observedAttributes: Array<String> = emptyArray()
): CustomElementFactory<T> {
    return object : CustomElementFactory<T> {
        override val tagName: String = tagName
        override val create: () -> T = factory
        override val observedAttributes: Array<String> get() = observedAttributes
        override val className: String = T::class.simpleName ?: error("simpleName not found")
        override fun createElement(): HTMLElement = document.createElement(tagName) as HTMLElement
    }
}

abstract class CustomElementMeta(val factory: CustomElementFactory<*>)

class MyElement2 : AbsCustomElement() {

    companion object : CustomElementMeta(ceFactory("my-element2", ::MyElement2, arrayOf("size", "color")))

    override fun constr() {
        console.log("constructor MyElement2 - kotlin")
        element.attachShadow(ShadowRootInit(ShadowRootMode.OPEN))
        element.shadowRoot.asDynamic().innerHTML = "<h1>Hello World2</h1>"
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
    }
}

fun defineCustomElement(cem: CustomElementMeta) {
    val className = cem.factory.className
    window.asDynamic()["kotlin_constructor_$className"] = cem.factory.create
    val code = _customElement
        .replace("#ClassName", className)
        .replace("#tagName", cem.factory.tagName)
        .replace("#observedAttributes", cem.factory.observedAttributes.joinToString { "\"$it\"" })
    console.log(code)
    eval(code)
}

//language=JavaScript
val _customElement = """
    class #ClassName extends HTMLElement {
        static observedAttributes = [ #observedAttributes ];
        
        constructor() {
            super();
//            this.attachShadow({ mode: "open" });
            console.log('constructor #ClassName - javascript');
            this._kt = window.kotlin_constructor_#ClassName();
            this._kt._element = this;
            this._kt.constr();
            console.log(this._kt);
        }

        connectedCallback() {
            console.log(this._kt);
            this._kt.connectedCallback();
        }

        disconnectedCallback() {
            this._kt.disconnectedCallback();
        }

        adoptedCallback() {
            this._kt.adoptedCallback();
        }

        attributeChangedCallback(name, oldValue, newValue) {
            this._kt.attributeChangedCallback(name, oldValue, newValue);
        }
    }

    customElements.define('#tagName', #ClassName);
    window.#ClassName = #ClassName;
"""

@JsExport
open class AbsCustomElement {

    var _element: HTMLElement? = null
    val element: HTMLElement get() = _element ?: error("element not set")

    open fun constr() {}

    open fun connectedCallback() {}

    open fun disconnectedCallback() {}

    open fun adoptedCallback() {}

    open fun attributeChangedCallback(name: String, oldValue: String, newValue: String) {}
}