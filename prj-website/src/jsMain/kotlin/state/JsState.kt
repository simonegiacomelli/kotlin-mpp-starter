package state


import accesscontrol.Anonymous
import accesscontrol.Session
import accesscontrol.UserAbs
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.coroutines.CoroutineScope
import menu.Menu
import org.w3c.dom.get
import org.w3c.dom.set
import pages.LoaderWidget
import pages.bootstrap.NavbarWidget
import pages.bootstrap.OffcanvasWidget
import pages.bootstrap.ToastStackWidget
import pages.bootstrap.TreeWidget
import rpc.apiDispatcher
import widget.HolderWidget

val body = document.getElementById("root") ?: document.body!!

class JsState : ClientState {
    override fun toast(message: String) = kotlin.run { widgets.toastStack.showToast(message) }
    override val ApiBaseUrl: String = ""

    override val session_id: String? get() = localStorage["id"]
    override val user: UserAbs get() = sessionOrNull?.user ?: Anonymous

    override suspend fun dispatch(name: String, payload: String): String = apiDispatcher(name, payload)
    override fun launch(block: suspend CoroutineScope.() -> Unit): Unit = run { coroutine.launch(block = block) }

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
    val navbar = NavbarWidget()
    val holder get() = navbar.mainHolder
    val offcanvas = OffcanvasWidget()
    val toastStack = ToastStackWidget()
    val menu = TreeWidget<Menu>()
}
