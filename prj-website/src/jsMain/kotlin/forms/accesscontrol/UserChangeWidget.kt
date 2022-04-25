package forms.accesscontrol

import org.w3c.dom.HTMLElement
import state.state
import widget.Widget
import widgets.FormBuilderWidget

class UserChangeWidget : Widget(//language=HTML
    """
<h5>Change your own pw</h5>    
<div id='divForm'></div>
<button id='btnChange' type="submit" class="btn btn-primary">Change password</button>

"""
) {
    private val divForm by this { FormBuilderWidget() }
    private val btnChange: HTMLElement by this

    private data class ChangePassword(
        var oldPassword: String = "",
        var newPassword1: String = "",
        var newPassword2: String = ""
    )

    private val changePassword = ChangePassword()

    override fun afterRender(): Unit = run {
        btnChange.onclick = { state.toast("NOT IMPLEMENTED YET! $changePassword") }
        divForm.apply {
            bind(changePassword, ChangePassword::oldPassword).label = "Old password"
            bind(changePassword, ChangePassword::newPassword1).label = "New password"
            bind(changePassword, ChangePassword::newPassword2).label = "Repeat new password"
        }
    }
}