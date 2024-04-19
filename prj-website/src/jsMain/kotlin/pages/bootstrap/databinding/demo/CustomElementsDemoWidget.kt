package pages.bootstrap.databinding.demo

import org.w3c.dom.*
import widget.*

class CustomElementsDemoWidget : Widget(
    // language=HTML
    """
    <my-element></my-element>
    div1:
    <div id="div1"><my-element id="ele1"></my-element></div>
    div2:
    <div id="div2"></div>
    <br><br>
    <button id="btn1" class="btn btn-primary">Change div</button>
"""
) {

    private val btn1: HTMLButtonElement by this
    private val div1: HTMLElement by this
    private val div2: HTMLElement by this
    private val ele1: HTMLElement by this

    companion object {
        init {
            defineCustomElement(MyElement)
        }
    }

    override fun afterRender() {
        btn1.onclick = {
            val empty = if (div1.childElementCount == 0) div1 else div2
            empty.appendChild(ele1)
            Unit
        }
    }
}


class MyElement : AbsCustomElement() {

    companion object : CEMeta<MyElement> by ceMeta("my-element", ::MyElement, arrayOf("size", "color"))

    var size by attributes
    var color by attributes

    override fun constr() {
        console.log("constructor MyElement - kotlin")
        element.attachShadow(ShadowRootInit(ShadowRootMode.OPEN))
        updateText()
    }

    private fun updateText() {
        val sizeInt = size?.toIntOrNull() ?: 1
        val s = sizeInt.coerceAtMost(10).coerceAtLeast(1)
        val c = color.let {
            if (it == null) "" else """ style="color: $it;" """
        }
        element.shadowRoot.asDynamic().innerHTML = "<h$s$c>Hello World2</h$s>"
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