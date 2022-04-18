package pages.bootstrap

import extensions.tbodyFirst
import extensions.td
import extensions.tr
import kotlinx.dom.clear
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

    fun render() {
        tbody.clear()
        appendMenu(null, 0)
    }

    private fun appendMenu(parent: E?, indent: Int) {
        onGetChildren(parent).forEach { element ->
            tbody.tr {
                td("&nbsp".repeat(8 * indent) + onCaption(element))
                onclick = { onElementClick(element) }
            }
            appendMenu(element, indent + 1)
        }
    }

}