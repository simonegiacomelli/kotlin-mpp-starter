package state


import accesscontrol.Session
import kotlinx.browser.localStorage
import kotlinx.coroutines.CoroutineScope
import org.w3c.dom.get
import org.w3c.dom.set
import pages.bootstrap.ToastWidget
import rpc.apiDispatcher
import utils.launchJs

fun installClientHandler() {
    val jsState = JsState()
    stateOrNull = { jsState }
}

class JsState : ClientState {
    override fun toast(message: String) = kotlin.run { ToastWidget.show(message) }
    override val ApiBaseUrl: String = ""

    override val session_id: String? get() = localStorage["id"]
    override suspend fun dispatch(name: String, payload: String): String = apiDispatcher(name, payload)
    override suspend fun launch(block: suspend CoroutineScope.() -> Unit): Unit = run { launchJs(block = block) }

    override var sessionOrNull: Session? = null
        set(value) {
            localStorage.removeItem("id")
            value?.apply { localStorage["id"] = id }
            field = value
        }

    override fun spinner(function: suspend CoroutineScope.() -> Unit) = dom.spinner(function)

}
