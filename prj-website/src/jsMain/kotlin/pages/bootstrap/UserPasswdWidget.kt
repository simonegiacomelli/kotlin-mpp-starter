package pages.bootstrap

import api.names.ApiAcUserPasswdRequest
import databinding.StringBridge
import databinding.bind
import org.w3c.dom.HTMLElement
import pages.bootstrap.commonwidgets.InputGroupWidget
import rpc.send
import state.state
import widget.Widget

class UserPasswdWidget : Widget(//language=HTML
    """
<h5>Set User password</h5>    
<div id='div1'></div>
<div id='div2'></div>
<button id='btnCreate' type="submit" class="btn btn-primary">Create user</button>
"""
) {
    private val div1 by this { InputGroupWidget() }
    private val div2 by this { InputGroupWidget() }
    private val btnCreate: HTMLElement by this

    private class Credential(var username: String, var password: String)

    override fun afterRender() = Credential("", "").run {
        div1.addon.innerHTML = "Username"
        div2.addon.innerHTML = "Password"
        bind(this, Credential::username, StringBridge(div1.input))
        bind(this, Credential::password, StringBridge(div2.input))
        btnCreate.onclick = {
            state.spinner {
                val msg = ApiAcUserPasswdRequest(username, password).send().message
                state.toast(msg)
            }
        }
    }
}