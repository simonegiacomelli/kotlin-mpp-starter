package pages.forms

import api.oneway.ApiNotifyHtmlChange
import browserTopic
import kotlinx.browser.document
import widget.Widget

class HtmlSignalWidget : Widget(//language=HTML
    """<h2>I'm HtmlWidget</h2>       
    """.trimMargin()
) {
    init {
        explicitContainer = document.createElement("span")
    }

    companion object {
        val shared by lazy { HtmlSignalWidget() }
    }

    override fun afterRender() {
        browserTopic.subscribe<ApiNotifyHtmlChange> {
            container.innerHTML = it.html
        }
    }
}
