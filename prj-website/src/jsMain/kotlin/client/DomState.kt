package client


import api.names.ApiAcSession
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import org.w3c.dom.get
import org.w3c.dom.set
import rpc.apiDispatcher
import utils.launchJs

fun installClientHandler() {
    clientStateOrNull = { state }
}

class DomState : State {
    override fun toast(message: String) = window.alert(message)
    override val ApiBaseUrl: String = ""

    override val session_id: String? get() = localStorage["id"]
    override suspend fun dispatch(name: String, payload: String): String = apiDispatcher(name, payload)
    override suspend fun launch(block: suspend CoroutineScope.() -> Unit): Unit = run { launchJs(block = block) }

    override var sessionOrNull: ApiAcSession? = null
        set(value) {
            localStorage.removeItem("id")
            value?.apply { localStorage["id"] = id }
            field = value
        }

    override fun spinner(function: suspend CoroutineScope.() -> Unit) = dom.spinner(function)

}


val state = DomState()