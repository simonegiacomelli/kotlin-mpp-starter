package forms.accesscontrol

import api.names.AcUser
import api.names.ApiAcUserListRequest
import grid.GridWidget
import grid.asProperty
import rpc.send
import state.state
import widget.Widget

class UserListWidget : Widget(//language=HTML
    """<h5>Users list</h5>
 <div id='table1'></div>    
"""
) {
    private val table1 by this{ GridWidget<AcUser>() }

    override fun afterRender() = load()

    private fun load(userId: Int? = null): Unit = state.spinner {
        table1.properties = AcUser.properties().map { it.asProperty() }.toMutableList()
        table1.elements = ApiAcUserListRequest().send().users
        table1.onElementClick = { show(UserEditWidget(element) { load(element.id) }) }
        table1.focusedElementChangeOnClick = true
        table1.focusedElement = table1.elements.firstOrNull { it.id == userId }
        table1.render()

    }

}

