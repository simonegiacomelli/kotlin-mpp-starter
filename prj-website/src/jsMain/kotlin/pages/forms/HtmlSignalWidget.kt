package pages.forms

import api.oneway.ApiNotifyHtmlChange
import browserTopic
import extensions.span
import widget.Widget

class HtmlSignalWidget : Widget(//language=HTML
    """<h2>I'm HtmlWidget</h2>       
    """.trimMargin()
) {
    init {
        explicitContainer = span()
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
