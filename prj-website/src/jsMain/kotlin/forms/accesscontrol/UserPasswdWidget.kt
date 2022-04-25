package forms.accesscontrol

import api.names.ApiAcUserPasswdRequest
import org.w3c.dom.HTMLElement
import rpc.send
import state.state
import widget.Widget
import widgets.FormBuilderWidget

class UserPasswdWidget : Widget(//language=HTML
    """
<h5>Set User password</h5>    
<div id='divForm'></div>
<button id='btnCreate' type="submit" class="btn btn-primary">Create user</button>
"""
) {
    private val divForm by this { FormBuilderWidget() }
    private val btnCreate: HTMLElement by this

    private val credential = Credential()

    override fun afterRender(): Unit = run {
        btnCreate.onclick = {
            state.spinner {
                val msg = credential.run { ApiAcUserPasswdRequest(username, password).send().message }
                state.toast(msg)
            }
        }
        divForm.apply {
            bind(credential, Credential::username).label = "Username"
            bind(credential, Credential::password).label = "Password"
        }
    }
}