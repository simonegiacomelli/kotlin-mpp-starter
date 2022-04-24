package grid

import extensions.span
import widget.Widget
import widget.containerElement

open class IconToolWidget(cssClass: String, title: String, val block: () -> Unit) : Widget(//language=HTML
    """
    <i class="$cssClass" title="$title"></i>   
    """.trimIndent()
) {
    init {
        explicitContainer = span()
    }

    override fun afterRender() {
        containerElement.onmousedown = {
            if (it.detail > 1) it.preventDefault() // prevent text selection
        }
        containerElement.onclick = { block() }
    }
}