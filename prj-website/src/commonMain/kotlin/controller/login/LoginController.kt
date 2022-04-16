package controller.login

import accesscontrol.Session
import api.names.ApiAcLoginRequest
import api.names.ApiAcSessionResponse
import api.names.ApiAcVerifySessionRequest
import api.names.Credential
import client.State
import rpc.send

class LoginController(
    state: State, val credential: () -> Credential, val close: () -> Unit
) : State by state {

    fun loginClick() {
        spinner {
            ApiAcLoginRequest(credential()).send().also { processResponse(it) }
        }
    }

    suspend fun verifySessionState() {
        val id = session_id ?: return
        val session = ApiAcVerifySessionRequest(id).send().session
        if (session != null) sessionOk(session)
    }

    private fun processResponse(response: ApiAcSessionResponse) {
        val session = response.session ?: return failedLogin()
        sessionOk(session)
    }

    private fun sessionOk(session: Session) {
        sessionOrNull = session
        close()
        toast("Sessione valida " + session.id)
    }

    private fun failedLogin() {
        toast("Login failed")
    }
}