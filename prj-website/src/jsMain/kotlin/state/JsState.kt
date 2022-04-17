package state


import accesscontrol.Session
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.coroutines.CoroutineScope
import org.w3c.dom.get
import org.w3c.dom.set
import pages.LoaderWidget
import pages.bootstrap.MenuWidget
import pages.bootstrap.NavbarWidget
import pages.bootstrap.OffcanvasWidget
import pages.bootstrap.ToastWidget
import rpc.apiDispatcher
import utils.launchJs
import widget.HolderWidget

fun installClientHandler(): JsState {
    val jsState = JsState()
    stateOrNull = { jsState }
    return jsState
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

    override fun spinner(function: suspend CoroutineScope.() -> Unit) = LoaderWidget.shared.spinner(function)

    val body = document.getElementById("root") ?: document.body!!
    val widgets = Widgets()
}

class Widgets {
    val holder = HolderWidget()
    val navbar = NavbarWidget()
    val offcanvas = OffcanvasWidget()
    val menu = MenuWidget()
}
