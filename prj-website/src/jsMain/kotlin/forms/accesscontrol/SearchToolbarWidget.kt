package forms.accesscontrol

import extensions.extVisible
import grid.AfterRenderEvent
import grid.GridWidget
import org.w3c.dom.HTMLInputElement
import state.spinner
import utils.forward
import widget.Widget

class SearchToolbarWidget<E>(private val grid: GridWidget<E>) :
    Widget( //language=HTML
        """<input id="input" type="search" class="form-control" placeholder="Search..." style="min-width: 8em; width:30vw"> """
    ) {
    private val input: HTMLInputElement by this
    val value: String by forward { input::value }
    override fun afterRender() {
        input.oninput = { setVisibility() }
        grid.addObserver { _: AfterRenderEvent<E> -> setVisibility() }
    }

    private fun setVisibility() = spinner {
        grid.elements.forEach { element ->
            grid.elementInfoOrNull(element)?.also {
                it.tr.extVisible = isVisibile(element)
            }
        }
    }

    private fun isVisibile(element: E): Boolean {
        if (value.isBlank()) return true
        val lowercaseSearch = value.lowercase()
        val str = grid.properties.map { it.get(element) }.joinToString(" ")
        return str.lowercase().contains(lowercaseSearch)
    }

}