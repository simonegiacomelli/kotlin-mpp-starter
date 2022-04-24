package forms.telemetry

import grid.GridWidget
import grid.asProperty
import rpc.send
import state.state
import telemetry.api.ApiTmListEventsRequest
import telemetry.api.TmEvent
import widget.Widget

class TmEventsWidget : Widget(//language=HTML
    """
<h5>Telemetry Events</h5>
<div id='table1'></div>
"""
) {
    private val table1 by this{ GridWidget<TmEvent>() }

    override fun afterRender() = state.spinner {
        table1.elements = ApiTmListEventsRequest().send().events
        table1.properties = listOf(
            TmEvent::id,
            TmEvent::type_id,
            TmEvent::arguments,
            TmEvent::created_at,
        ).map { it.asProperty() }.toMutableList()
        table1.render()
    }
}