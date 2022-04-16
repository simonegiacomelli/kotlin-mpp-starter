package client


import api.names.ApiAcSession
import kotlinx.browser.window

fun installClientHandler() {
    clientStateOrNull = { state }
}

class State : ClientState {
    override fun toast(message: String) = window.alert(message)
    override val ApiBaseUrl: String = ""
    override val session_id: String? get() = sessionOrNull?.id
    var sessionOrNull: ApiAcSession? = null
}


val state = State()