package controller.login

import accesscontrol.Session
import api.names.ApiAcLoginRequest
import api.names.ApiAcSessionResponse
import api.names.ApiAcVerifySessionRequest
import api.names.Credential
import rpc.send
import state.ClientState

class LoginController(
    state: ClientState, val credential: () -> Credential, val close: () -> Unit
) : ClientState by state {

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
        toast("Welcome ${session.user.username}")
    }

    private fun failedLogin() {
        toast("Login failed")
    }
}