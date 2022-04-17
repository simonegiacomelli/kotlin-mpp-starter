package forms.login

import api.names.Credential
import controller.login.LoginController
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import state.state
import utils.launchJs
import widget.Widget

class LoginWidget(val onSessionOk: () -> Unit) : Widget(html) {
    private val floatingInput: HTMLInputElement by this
    private val floatingPassword: HTMLInputElement by this
    private val btnSubmit: HTMLButtonElement by this

    override fun afterRender() {
        val controller =
            LoginController(state = state, onCredential = { getCredential() }, onSessionOk = { onSessionOk() })
        launchJs { controller.verifySessionState() }
        btnSubmit.onclick = { it.preventDefault(); it.stopPropagation(); controller.loginClick() }
    }

    private fun getCredential() = Credential(floatingInput.value, floatingPassword.value)

}

private val html = //language=HTML
    """
<style>
    .form-body {
        display: flex;
        align-items: center;
        padding-top: 40px;
        padding-bottom: 40px;
    }

    .form-signin {
        width: 100%;
        max-width: 330px;
        padding: 15px;
        margin: auto;
    }

    .form-signin .checkbox {
        font-weight: 400;
    }

    .form-signin .form-floating:focus-within {
        z-index: 2;
    }

    .form-signin input[type="email"] {
        margin-bottom: -1px;
        border-bottom-right-radius: 0;
        border-bottom-left-radius: 0;
    }

    .form-signin input[type="password"] {
        margin-bottom: 10px;
        border-top-left-radius: 0;
        border-top-right-radius: 0;
    }
    .top-image {
        object-fit: contain;
        width: 100%;
        height: 150px;
    }
</style>
<div class="form-signin">
    <form>
        <img class="top-image" src="img/login.png" alt="">
        <h1 class="h3 mb-3 fw-normal">Please sign in</h1>

        <div class="form-floating">
            <input type="text" class="form-control" id="floatingInput" placeholder="Username">
            <label for="floatingInput">Username</label>
        </div>
        <div class="form-floating">
            <input type="password" class="form-control" id="floatingPassword" placeholder="Password">
            <label for="floatingPassword">Password</label>
        </div>

        <button class="w-100 btn btn-lg btn-primary" id="btnSubmit">Sign in</button>
        <p class="mt-5 mb-3 text-muted">&copy; 2021–${Clock.System.now().toLocalDateTime(TimeZone.UTC).year} v0.4.0</p>
    </form>
</div>
""".trimIndent()