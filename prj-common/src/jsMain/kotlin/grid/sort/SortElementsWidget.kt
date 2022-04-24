package grid.sort

import extensions.input
import grid.GridWidget
import grid.IconToolWidget
import grid.ValueEvent
import grid.property
import kotlinx.dom.clear
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTableCellElement
import widget.containerElement

internal data class ElementAddon<E>(
    var checked: Boolean, val checkbox: HTMLInputElement, val element: E
) {
    lateinit var checkBoxCell: HTMLTableCellElement
}

//private const val arrowUp = "↑-\u2190"
//private val arrowUp = "2B06".toInt(16).toChar() //⬆
//private val arrowDown = "2B07".toInt(16).toChar() //⬇

private val arrowUp = "25B2".toInt(16).toChar() //▲
private val arrowDown = "25BC".toInt(16).toChar() //▼

//private const val arrowUp = "⬆-\u2B06"
//private const val arrowDown = "⬇-\u2B07"

open class SortElementsWidget<E> : GridWidget<E>() {
    val grid = this

    val selectedElements: List<E> get() = elements.filter { isSelected(it) }
    val unselectedElements: List<E> get() = elements.filterNot { isSelected(it) }
    private val addonMap = mutableMapOf<E, ElementAddon<E>>()
    private val btnUp = IconToolWidget("fa fa-arrow-up", "Muovi in su'") { moveUp() }
    private val btnDown = IconToolWidget("fa fa-arrow-down", "Muovi in giu'") { moveDown() }

    override fun afterRender() {
        super.afterRender()
        grid.toolbar.addAll(0, listOf(btnUp, btnDown))
    }

    private val selectedProperty = property(
        name = "selected", caption = "Selected", get = ::isSelected, onDataRender = ::onSelectCellRender
    )

    override fun render(): SortElementsWidget<E> = apply {

        if (properties.firstOrNull() != selectedProperty) properties.add(0, selectedProperty)

        grid.elements = elements
        grid.onElementClick = { focusedElement = element; render() }
        grid.sortableHead = false
        super.render()
    }

    private fun onSelectCellRender(valueEvent: ValueEvent<E>) = valueEvent.apply {
        cell.clear()
        cell.style.textAlign = "center"
        val info = addon(element)
        info.checkBoxCell = cell
        val checkbox = info.checkbox.apply {
            type = "checkbox"
            checked = isSelected(element)
            onclick = { it.stopPropagation(); selectionChange(element, checked) }
        }
        cell.append(checkbox)
        cell.onclick = { selectionChange(element, !checkbox.checked) }
    }

    private fun selectionChange(element: E, checked: Boolean) {
        setSelected(element, checked)
        render()
    }

    fun setSelected(element: E, checked: Boolean) = run { addon(element).checked = checked }

    private fun addon(element: E) = addonMap.getOrPut(element) {
        ElementAddon(false, input(), element)
    }

    private fun isSelected(element: E) = addon(element).checked


    private fun moveUp() = move(-1)

    private fun moveDown() = move(+1)

    private fun move(displacement: Int) {
        val els = elements.toMutableList()
        val selected = focusedElement ?: return
        val idx = els.indexOf(selected)
        val other = idx + displacement
        if (other < 0 || other >= els.size) return
        els[idx] = els[other]
        els[other] = selected
        elements = els
        render()
    }

    fun htmlCheckboxCellFor(element: E) = addon(element).checkBoxCell
    fun htmlCheckboxFor(element: E) = addon(element).checkbox

    fun htmlMoveUp(): HTMLElement = btnUp.containerElement
    fun htmlMoveDown(): HTMLElement = btnDown.containerElement
}


