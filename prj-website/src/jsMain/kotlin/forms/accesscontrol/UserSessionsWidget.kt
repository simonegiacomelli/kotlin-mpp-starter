package forms.accesscontrol

import api.names.AcSession
import api.names.ApiAcUserSessionsRequest
import grid.GridWidget
import grid.asProperty
import rpc.send
import state.state
import widget.Widget

class UserSessionsWidget : Widget(//language=HTML
    """<h5>User sessions</h5>
 <div id='table1'></div>    
"""
) {
    private val table1 by this{ GridWidget<AcSession>() }

    override fun afterRender() = state.spinner {
        table1.properties = AcSession.properties().map { it.asProperty() }.toMutableList()
        table1.elements = ApiAcUserSessionsRequest().send().sessions
        table1.render()
    }

}