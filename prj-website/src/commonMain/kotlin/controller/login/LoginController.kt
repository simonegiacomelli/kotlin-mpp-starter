package controller.login

import accesscontrol.Session
import api.names.ApiAcLoginRequest
import api.names.ApiAcSessionResponse
import api.names.Credential
import rpc.send
import state.ClientState

class LoginController(
    state: ClientState,
    val onCredential: () -> Credential,
    val onSessionOk: () -> Unit
) : ClientState by state {

    fun loginClick() {
        spinner {
            ApiAcLoginRequest(onCredential()).send().also { processResponse(it) }
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
    toast("Welcome ${session.user.username}")
}