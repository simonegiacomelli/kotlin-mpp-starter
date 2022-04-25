package forms.accesscontrol

import api.names.ApiAcUserCreateRequest
import databinding.Bindable
import org.w3c.dom.HTMLElement
import rpc.send
import state.state
import widget.Widget
import widgets.FormBuilderWidget

class UserCreateWidget : Widget(//language=HTML
    """
<h5>Create User</h5>    
<div id='divForm'></div>
<button id='btnCreate' type="submit" class="btn btn-primary">Create user</button>
"""
) {
    private val divForm by this { FormBuilderWidget() }
    private val btnCreate: HTMLElement by this

    private class Credential : Bindable() {
        var username: String by this("")
        var password: String by this("")
    }

    private val credential = Credential()

    override fun afterRender(): Unit = run {
        btnCreate.onclick = {
            state.spinner {
                val msg = credential.run { ApiAcUserCreateRequest(username, password).send().message }
                state.toast(msg)
            }
        }
        divForm.apply {
            bind(credential, Credential::username).label = "Username"
            bind(credential, Credential::password).label = "Password"
        }
    }
}