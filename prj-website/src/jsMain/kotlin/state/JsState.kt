package state


import accesscontrol.Anonymous
import accesscontrol.Session
import accesscontrol.UserAbs
import coroutine.launchJs
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.coroutines.CoroutineScope
import menu.Menu
import org.w3c.dom.get
import org.w3c.dom.set
import pages.LoaderWidget
import pages.bootstrap.Navbar2Widget
import pages.bootstrap.OffcanvasWidget
import pages.bootstrap.ToastWidget
import pages.bootstrap.TreeWidget
import rpc.apiDispatcher
import widget.HolderWidget

fun installClientHandler(): JsState {
    val jsState = JsState()
    stateOrNull = { jsState }
    return jsState
}

val body = document.getElementById("root") ?: document.body!!

class JsState : ClientState {
    override fun toast(message: String) = kotlin.run { ToastWidget.show(message) }
    override val ApiBaseUrl: String = ""

    override val session_id: String? get() = localStorage["id"]
    override val user: UserAbs get() = sessionOrNull?.user ?: Anonymous

    override suspend fun dispatch(name: String, payload: String): String = apiDispatcher(name, payload)
    override fun launch(block: suspend CoroutineScope.() -> Unit): Unit = run { launchJs(block = block) }

    override var sessionOrNull: Session? = null
        set(value) {
            localStorage.removeItem("id")
            value?.apply { localStorage["id"] = id }
            field = value
        }

    override fun spinner(function: suspend CoroutineScope.() -> Unit) = LoaderWidget.shared.spinner(function)

    val widgets = Widgets()
}

class Widgets {
    val rootHolder = HolderWidget()
    val navbar = Navbar2Widget()
    val holder get() = navbar.mainHolder
    val offcanvas = OffcanvasWidget()
    val menu = TreeWidget<Menu>()
}
