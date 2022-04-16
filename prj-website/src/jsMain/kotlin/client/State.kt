package client


import api.names.ApiAcSession
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.w3c.dom.get
import org.w3c.dom.set

fun installClientHandler() {
    clientStateOrNull = { state }
}

class State : ClientState {
    override fun toast(message: String) = window.alert(message)
    override val ApiBaseUrl: String = ""

    override val session_id: String? get() = localStorage["id"]

    var sessionOrNull: ApiAcSession? = null
        set(value) {
            localStorage.removeItem("id")
            value?.apply { localStorage["id"] = id }
            field = value
        }

}


val state = State()