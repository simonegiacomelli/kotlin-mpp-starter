import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import kotlin.reflect.KClass

fun defineCustomElemenTest() {
    defineCustomElement("my-element2", MyElement2::class) { MyElement2(it) }
    document.body?.append(document.createElement("my-element2"))
    console.log("defineCustomElemenTest")
}


class MyElement2(element: HTMLElement) : AbsCustomElement(element) {
    init {
        console.log("constructor MyElement2 - kotlin")
//        element.attachShadow(json("mode" to "open"))
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

inline fun <reified T : AbsCustomElement> defineCustomElement(
    tagName: String,
    clazz: KClass<T>,
    noinline constr: (HTMLElement) -> T
) {
    val className = T::class.simpleName
    window.asDynamic()["kotlin_constructor_$className"] = constr
    val code = _customElement.replace("#ClassName", className!!).replace("#tagName", tagName)
    console.log(code)
    eval(code)
}

//language=JavaScript
val _customElement = """
    class #ClassName extends HTMLElement {
        constructor() {
            super();
            this.attachShadow({ mode: "open" });
            console.log('constructor #ClassName - javascript');
            this._kt = window.kotlin_constructor_#ClassName(this);
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
open class AbsCustomElement(val element: HTMLElement) {

    open fun connectedCallback() {}

    open fun disconnectedCallback() {}

    open fun adoptedCallback() {}

    open fun attributeChangedCallback(name: String, oldValue: String, newValue: String) {}
}