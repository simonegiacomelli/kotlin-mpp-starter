package pages.bootstrap

import org.w3c.dom.HTMLElement
import version
import widget.HolderWidget
import widget.Widget

class Navbar2Widget(private val rootWidget: Widget = Widget("<h1>empty rootWidget</h1>")) : Widget(//language=HTML
    """

<!-- Image and text -->
<nav class="navbar fixed-top navbar-dark bg-dark">
 
    <button class="navbar-toggler border-0" type="button" id="navbarSideCollapse"
                aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
    </button>
    <a class="navbar-brand" href="#">Kotlin-mpp-starter $version</a>
</nav>
<!-- div class="nav-scroller bg-body shadow-sm" id="navBand" w-expand></div -->        
<main class="container p-2 pt-3" id="mainHolder"></main>        
    """.trimIndent()
) {
    val mainHolder by this { HolderWidget() }

    private val navbarSideCollapse: HTMLElement by this

    var onHamburgerClick: () -> Unit = {}
    override fun afterRender() {

        navbarSideCollapse.onclick = { it.stopPropagation();it.preventDefault(); onHamburgerClick() }

        mainHolder.show(rootWidget)

    }

}

