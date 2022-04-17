package pages.bootstrap

import extensions.tbodyFirst
import extensions.td
import extensions.tr
import menu.Menu
import menu.root
import org.w3c.dom.HTMLTableElement
import widget.Widget

class MenuWidget : Widget(//language=HTML
    """
<table class="table table-hover" id='idTable'>
    <tbody></tbody>
</table>
"""
) {
    private val idTable: HTMLTableElement by this

    override fun afterRender() {
        appendMenu(root, 0)
    }

    private fun appendMenu(parent: Menu, indent: Int) {
        parent.children.forEach { menu ->
            idTable.tbodyFirst().tr { td("&nbsp".repeat(8 * indent) + menu.caption) }
            appendMenu(menu, indent + 1)
        }
    }

}