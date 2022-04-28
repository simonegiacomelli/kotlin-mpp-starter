package forms.accesscontrol

import api.names.AcUser
import api.names.ApiAcUserListRequest
import api.names.ApiAcUserSaveRequest
import grid.GridWidget
import grid.asProperty
import org.w3c.dom.HTMLElement
import rpc.send
import state.spinner
import state.state
import widget.Widget
import widgets.FormBuilderWidget

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
        table1.onElementClick = { show(UserWidget(element) { load(element.id) }) }
        table1.focusedElementChangeOnClick = true
        table1.focusedElement = table1.elements.firstOrNull { it.id == userId }
        table1.render()

    }

}

class UserWidget(private val user: AcUser, private val onClose: () -> Unit) : Widget(//language=HTML
    """
<h5>User</h5>    
<div id='divForm'></div>
<button id='btnSave' type="submit" class="btn btn-primary">Save</button>
&nbsp;
<button id='btnCancel' type="submit" class="btn btn-primary">Cancel</button>
"""
) {
    private val divForm by this { FormBuilderWidget() }
    private val btnSave: HTMLElement by this
    private val btnCancel: HTMLElement by this


    override fun afterRender(): Unit = run {
        btnCancel.onclick = { doClose() }
        btnSave.onclick = { doSave() }
        divForm.apply {
            bind(user, AcUser::username)
            bind(user, AcUser::email)
            bind(user, AcUser::phone_number)
            bind(user, AcUser::lockout_enabled)
            bind(user, AcUser::lockout_end_date_utc)
        }
    }

    private fun doSave() = spinner {
        val response = ApiAcUserSaveRequest(user).send()
        if (response.ok)
            return@spinner doClose()

        state.toast(response.message)
    }

    private fun doClose() {
        close(); onClose()
    }
}