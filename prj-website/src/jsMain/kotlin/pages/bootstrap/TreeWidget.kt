package pages.bootstrap

import extensions.tbodyFirst
import extensions.td
import extensions.tr
import kotlinx.dom.clear
import org.w3c.dom.HTMLTableCellElement
import org.w3c.dom.HTMLTableElement
import widget.Widget

class TreeWidget<E> : Widget(//language=HTML
    """
<table class="table table-hover" id='idTable'>
    <tbody></tbody>
</table>
"""
) {
    private val idTable: HTMLTableElement by this
    private val tbody get() = idTable.tbodyFirst()
    var onGetChildren: (parent: E?) -> List<E> = { emptyList() }
    var onCaption: (E) -> String = { "$it" }
    var onElementClick: (E) -> Unit = { }
    var onCellRender: CellRender<E>.() -> Unit = {}

    fun render() {
        tbody.clear()
        appendMenu(null, 0)
    }

    private fun appendMenu(parent: E?, depth: Int) {
        onGetChildren(parent).forEach { element ->
            tbody.tr {
                val td = td("&nbsp".repeat(8 * depth) + onCaption(element))
                onclick = { onElementClick(element) }
                CellRender(element, td, depth).onCellRender()
            }
            appendMenu(element, depth + 1)
        }
    }
}

data class CellRender<E>(val element: E, val cell: HTMLTableCellElement, val depth: Int)