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

fun <T : AbsCustomElement> defineCustomElement(tagName: String, clazz: KClass<T>, constr: (HTMLElement) -> T) {
    val className = clazz::class.simpleName
    window.asDynamic()["python_constructor_$className"] = constr
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
            this._py = window.python_constructor_#ClassName(this);
            console.log(this._py);
        }

        connectedCallback() {
            console.log(this._py);
            this._py.connectedCallback();
        }

        disconnectedCallback() {
            this._py.disconnectedCallback();
        }

        adoptedCallback() {
            this._py.adoptedCallback();
        }

        attributeChangedCallback(name, oldValue, newValue) {
            this._py.attributeChangedCallback(name, oldValue, newValue);
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