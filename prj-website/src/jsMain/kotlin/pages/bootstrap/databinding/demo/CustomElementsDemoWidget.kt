package pages.bootstrap.databinding.demo

import org.w3c.dom.OPEN
import org.w3c.dom.ShadowRootInit
import org.w3c.dom.ShadowRootMode
import widget.*

class CustomElementsDemoWidget : Widget("<my-element></my-element>") {
    companion object {
        init {
            defineCustomElement(MyElement)
        }
    }
}


class MyElement : AbsCustomElement() {

    companion object : CEMeta<MyElement> by ceMeta("my-element", ::MyElement, arrayOf("size", "color"))

    var size by attributes

    override fun constr() {
        console.log("constructor MyElement - kotlin")
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