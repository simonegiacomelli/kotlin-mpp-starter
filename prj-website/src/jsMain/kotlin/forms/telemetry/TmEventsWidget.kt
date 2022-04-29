package forms.telemetry

import databinding.IntTarget
import databinding.LocalDateTimeBridge
import databinding.StringTarget
import databinding.bind
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
    """<h5>Telemetry Events</h5>
<div class="container">
    <div class="row">
        <div class="col">
            <div id='table1'></div>
        </div>
        <div class="col">
            <div id='table2'></div>
        </div>
    </div>
</div>
"""
) {
    private val table1 by this{ GridWidget<TmEvent>() }
    private val table2 by this{ GridWidget<TmEvent>() }

    override fun afterRender() {
        setupTable(table1)
        setupTable(table2)
        refresh()
    }

    private fun refresh() = state.spinner {
        val tables = listOf(table1, table2)
        val events = ApiTmListEventsRequest().send().events
        tables.forEach { it.elements = events; it.render() }
    }

    private fun setupTable(tab: GridWidget<TmEvent>) {
        tab.properties = listOf(
            TmEvent::id,
            TmEvent::type_id,
            TmEvent::arguments,
            TmEvent::created_at,
        ).map { it.asProperty() }.toMutableList()
        tab.onDataRender = {
            val op = when (propertyIndex) {
                0 -> false //bind(element, TmEvent::id, LongBridge(cell))
                1 -> bind(element, TmEvent::type_id, IntTarget(cell))
                2 -> bind(element, TmEvent::arguments, StringTarget(cell))
                3 -> bind(element, TmEvent::created_at, LocalDateTimeBridge(cell))
                else -> false
            }
            if (op != false) cell.contentEditable = "true"
        }
        tab.focusedElementChangeOnClick = true
        tab.onElementRender = {
            tr.addEventListener("focusin", {
                console.log("focusin event", it)
                grid.focusedElement = element
            })
        }
        tab.table.apply {
            tabIndex = -1
            Hotkey(this).add("F1") { tab.focusNext(+1) }
            Hotkey(this).add("F2") { tab.focusNext(-1) }
        }
        tab.toolbar.add(Widget("<span>\uD83D\uDD04 refresh</span>").apply {
            containerElement.onclick = { refresh() }
        })

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
