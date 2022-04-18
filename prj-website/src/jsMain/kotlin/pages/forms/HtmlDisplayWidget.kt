package pages.forms

import api.oneway.ApiNotifyHtmlChange
import browserTopic
import extensions.span
import widget.Widget

class HtmlDisplayWidget : Widget(//language=HTML
    """<h2>I'm HtmlWidget</h2>       
    """.trimMargin()
) {
    init {
        explicitContainer = span()
    }

    companion object {
        val shared by lazy { HtmlDisplayWidget() }
    }

    override fun afterRender() {
        browserTopic.subscribe<ApiNotifyHtmlChange> {
            container.innerHTML = it.html
        }
    }
}
