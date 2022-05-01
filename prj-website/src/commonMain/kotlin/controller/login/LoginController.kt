package controller.login

import accesscontrol.Session
import api.names.ApiAcLoginRequest
import api.names.ApiAcSessionResponse
import rpc.send
import state.ClientState

class LoginController(
    state: ClientState,
    val onRequest: () -> ApiAcLoginRequest,
    val onSessionOk: () -> Unit
) : ClientState by state {

    fun loginClick() {
        spinner {
            onRequest().send().also { processResponse(it) }
        }
    }


    private fun processResponse(response: ApiAcSessionResponse) {
        val session = response.session ?: return failedLogin()
        sessionOk(session)
        onSessionOk()
    }


    private fun failedLogin() {
        toast("Login failed")
    }
}

fun ClientState.sessionOk(session: Session) {
    sessionOrNull = session
}