package forms.login

import api.names.*
import client.state
import forms.toast.toast
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import pages.spinner
import rpc.send
import widget.Widget

class LoginWidget : Widget(html) {
    private val floatingInput: HTMLInputElement by this
    private val floatingPassword: HTMLInputElement by this
    private val btnSubmit: HTMLButtonElement by this

    override fun afterRender() {
        spinner {
            btnSubmit.disabled = true
            kotlin.runCatching { verifySessionState() }
            btnSubmit.disabled = false
            btnSubmit.onclick = { loginClick() }
        }
    }

    private fun loginClick() {
        spinner {
            ApiAcLoginRequest(Credential(floatingInput.value, floatingPassword.value))
                .send().also { processResponse(it) }

        }
    }

    private suspend fun verifySessionState() {
        val id = state.session_id ?: return
        val session = ApiAcVerifySessionRequest(id).send().session
        if (session != null)
            sessionOk(session)
    }

    private fun processResponse(response: ApiAcSessionResponse) {
        val session = response.session ?: return failedLogin()
        sessionOk(session)
    }

    private fun sessionOk(session: ApiAcSession) {
        state.sessionOrNull = session
        close()
        toast("Sessione creata " + session.id)
    }

    private fun failedLogin() {
        toast("Login failed")
    }
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
            <input type="email" class="form-control" id="floatingInput" placeholder="name@example.com">
            <label for="floatingInput">Email address</label>
        </div>
        <div class="form-floating">
            <input type="password" class="form-control" id="floatingPassword" placeholder="Password">
            <label for="floatingPassword">Password</label>
        </div>

        <div class="checkbox mb-3">
            <label>
                <input type="checkbox" value="remember-me"> Remember me
            </label>
        </div>
        <button class="w-100 btn btn-lg btn-primary" type="submit" id="btnSubmit">Sign in</button>
        <p class="mt-5 mb-3 text-muted">&copy; 2021–${Clock.System.now().toLocalDateTime(TimeZone.UTC).year}</p>
    </form>
</div>
""".trimIndent()