package pages.forms

import api.oneway.ApiNotifyHtmlChange
import browserTopic
import keyboard.Hotkey
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLTextAreaElement
import widget.Widget

class HtmlEditorWidget : Widget(    //language=HTML
    """<h2>I'm designer</h2>
        |<textarea id="input" style="width: 100%"></textarea>
        |<br>
        |<button id="btn">send</button>
        |<br>
    """.trimMargin()
) {
    private val input: HTMLTextAreaElement by this
    private val btn: HTMLButtonElement by this

    override fun afterRender() {
        btn.onclick = { onClick() }
        Hotkey(input).add("CTRL-Enter") { onClick() }

    }

    private fun onClick() {
        browserTopic.publish(ApiNotifyHtmlChange(input.value))
    }

}