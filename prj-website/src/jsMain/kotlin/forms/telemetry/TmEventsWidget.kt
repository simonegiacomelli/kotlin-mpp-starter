package forms.telemetry

import extensions.cells
import grid.GridWidget
import grid.asProperty
import keyboard.Hotkey
import rpc.send
import state.state
import telemetry.api.ApiTmListEventsRequest
import telemetry.api.TmEvent
import widget.Widget
import widget.containerElement

class TmEventsWidget : Widget(//language=HTML
    """
<h5>Telemetry Events</h5>
<div id='table1'></div>
"""
) {
    private val table1 by this{ GridWidget<TmEvent>() }

    override fun afterRender() {
        table1.properties = listOf(
            TmEvent::id,
            TmEvent::type_id,
            TmEvent::arguments,
            TmEvent::created_at,
        ).map { it.asProperty() }.toMutableList()
        table1.onDataRender = { cell.contentEditable = "true" }
        table1.focusedElementChangeOnClick = true
        table1.onElementRender = {
//            tr.tabIndex = -1
            tr.addEventListener("focusin", {
                console.log("focusin event", it)
                grid.focusedElement = element
            })
        }
        table1.table.apply {
            tabIndex = -1
            Hotkey(this).add("F1") { table1.focusNext(+1) }
            Hotkey(this).add("F2") { table1.focusNext(-1) }
        }
        table1.toolbar.add(Widget("<span>\uD83D\uDD04 refresh</span>").apply {
            containerElement.onclick = { refresh() }
        })
        refresh()
    }

    private fun refresh() = state.spinner {
        table1.elements = ApiTmListEventsRequest().send().events
        table1.render()
    }
}

private fun <E> GridWidget<E>.focusNext(amount: Int) {
    val fe = focusedElement
    val index = if (fe != null) (elementInfoFor(fe).index + amount) else 0
    val newFocus = if (index in elements.indices) elements[index] else return
    focusedElement = newFocus
    htmlRowFor(newFocus).cells().firstOrNull()?.apply {
        focus()
    }
}
