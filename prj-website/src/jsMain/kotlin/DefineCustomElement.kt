import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

fun defineCustomElemenTest() {
    defineCustomElement("my-element2", MyElement2::class)
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

fun defineCustomElement(tagName: String, clazz: dynamic) {
    val className = clazz::class.simpleName
    window.asDynamic()["python_constructor_$className"] = clazz
    val code = _customElement.replace("#ClassName", className!!).replace("#tagName", tagName)
    console.log(code)
    eval(code)
}

//language=JavaScript
val _customElement = """
    class #ClassName extends HTMLElement {
        constructor() {
            super();
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

open class AbsCustomElement(val element: dynamic) {

    open fun connectedCallback() {}

    open fun disconnectedCallback() {}

    open fun adoptedCallback() {}

    open fun attributeChangedCallback(name: String, oldValue: String, newValue: String) {}
}