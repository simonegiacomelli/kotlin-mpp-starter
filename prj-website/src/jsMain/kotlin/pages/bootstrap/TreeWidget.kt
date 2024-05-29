package pages.bootstrap

import extensions.tbodyFirst
import extensions.td
import extensions.tr
import kotlinx.dom.clear
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTableCellElement
import org.w3c.dom.HTMLTableElement
import widget.Widget

class TreeWidget<E> : Widget(//language=HTML
    """
<input type='search' id='idSearch' placeholder='search the menu...'>
<table class="table table-hover" id='idTable'>
    <tbody></tbody>
</table>
"""
) {
    private val idTable: HTMLTableElement by this
    private val idSearch: HTMLInputElement by this
    private val tbody get() = idTable.tbodyFirst()
    var onGetChildren: (parent: E?) -> List<E> = { emptyList() }
    var onCaption: (E) -> String = { "$it" }
    var onElementClick: (E) -> Unit = { }
    var onCellRender: CellRender<E>.() -> Unit = {}

    fun render() {
        renderMenu()
        idSearch.oninput = { renderMenu() }
    }

    private fun renderMenu() {
        tbody.clear()
        appendMenu(null, 0)
    }

    private fun appendMenu(parent: E?, depth: Int) {
        fun accept(element: E): Boolean {
            if (depth == 0) return true
            val search = idSearch.value
            return search.isEmpty() || onCaption(element).contains(search, ignoreCase = true)
        }
        onGetChildren(parent).forEach { element ->
            if (accept(element))
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