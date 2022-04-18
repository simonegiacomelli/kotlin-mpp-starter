package pages.bootstrap

import keyboard.HotkeyWindow
import org.w3c.dom.HTMLElement
import pages.forms.HtmlDisplayWidget
import pages.forms.HtmlEditorWidget
import version
import widget.HolderWidget
import widget.Widget

class NavbarMenuWidget(private val rootWidget: Widget = Widget("<h1>empty rootWidget</h1>")) : Widget(//language=HTML
    """

<nav class="navbar navbar-expand-lg fixed-top navbar-dark bg-dark" aria-label="Main navigation">
    <div class="container-fluid">
        <button class="navbar-toggler p-0 border-0" type="button" id="navbarSideCollapse"
                aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <a class="navbar-brand" href="#">Kotlin-mpp-starter $version</a>

        <div class="navbar-collapse offcanvas-collapse" id="navbarsExampleDefault">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="#">Dashboard</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#">Notifications</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#">Profile</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" >Menu item1</a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="dropdown01" data-bs-toggle="dropdown"
                       aria-expanded="false">Settings</a>
                    <ul class="dropdown-menu" aria-labelledby="dropdown01">
                        <li><a class="dropdown-item" href="#" id="menuHtml1">Toggle html host</a></li>
                        <li><a class="dropdown-item" href="#">Another action</a></li>
                        <li><a class="dropdown-item" href="#">Something else here</a></li>
                    </ul>
                </li>
            </ul>
            <form class="d-flex">
                <input class="form-control me-2" type="search" placeholder="Search" aria-label="Search">
                <button class="btn btn-outline-success" type="submit">Search</button>
            </form>
        </div>
    </div>
</nav>
<!-- div class="nav-scroller bg-body shadow-sm" id="navBand" w-expand></div -->        
<main class="container" id="mainHolder"></main>        
    """.trimIndent()
) {
    val mainHolder by this { HolderWidget() }

    private val navbarSideCollapse: HTMLElement by this
    private val navbarsExampleDefault: HTMLElement by this
    private val menuHtml1: HTMLElement by this
    val defaultHamburgerClick = { navbarsExampleDefault.classList.toggle("open"); Unit }
    var onHamburgerClick: () -> Unit = defaultHamburgerClick
    override fun afterRender() {

        navbarSideCollapse.onclick = { onHamburgerClick() }
        menuHtml1.onclick = { toggle() }

        mainHolder.show(rootWidget)
        val designerWidget = HtmlEditorWidget()


        HotkeyWindow
//            .add("F2") { mainHolder.show(designerWidget) }
//            .add("F3", "~", "SHIFT-~") { it.stopPropagation(); it.preventDefault(); toggle() }
            .add("SHIFT-F4") { mainHolder.container.classList.toggle("container") }
            .add("Escape") { mainHolder.closeCurrent() }
    }

    private fun toggle() {
        if (mainHolder.stack.lastOrNull() == HtmlDisplayWidget.shared)
            mainHolder.closeCurrent()
        else mainHolder.show(HtmlDisplayWidget.shared)

    }
}

