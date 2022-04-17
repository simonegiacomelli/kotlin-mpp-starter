package pages.bootstrap

import extensions.tbodyFirst
import extensions.td
import extensions.tr
import kotlinx.dom.clear
import menu.Menu
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
    private val tbody get() = idTable.tbodyFirst()
    var onMenuClick: (Menu) -> Unit = { }

    fun setMenu(root: Menu) {
        tbody.clear()
        appendMenu(root, 0)
    }

    private fun appendMenu(parent: Menu, indent: Int) {
        parent.children.forEach { menu ->
            tbody.tr {
                td("&nbsp".repeat(8 * indent) + menu.caption)
                onclick = { onMenuClick(menu) }
            }
            appendMenu(menu, indent + 1)
        }
    }

}