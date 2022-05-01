package forms.accesscontrol

import api.names.AcRole
import api.names.ApiAcRolesRequest
import grid.GridWidget
import grid.asProperty
import rpc.send
import state.state
import widget.Widget

class RolesWidget : Widget(//language=HTML
    """<h5>Roles</h5>
 <div id='table1'></div>    
"""
) {
    private val table1 by this{ GridWidget<AcRole>().addSearchToolbar() }

    override fun afterRender() = state.spinner {
        table1.focusedElementChangeOnClick = true
        table1.properties = AcRole.properties().map { it.asProperty() }.toMutableList()
        table1.elements = ApiAcRolesRequest().send().roles
        table1.render()
    }

}