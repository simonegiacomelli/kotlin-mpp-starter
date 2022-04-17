package pages.bootstrap

import keyboard.HotkeyWindow
import kotlinx.dom.clear
import org.w3c.dom.HTMLElement
import utils.forward
import widget.Widget
import widget.containerElement

class OffcanvasWidget : Widget(//language=HTML
    """
<button id="btnToggle" style="display: none" data-bs-toggle="offcanvas" data-bs-target="#offcanvasScrolling"
        aria-controls="offcanvasScrolling"></button>

<div class="offcanvas offcanvas-start" data-bs-scroll="true" data-bs-backdrop="false" tabindex="-1"
     id="offcanvasScrolling" aria-labelledby="offcanvasScrollingLabel">
    <div class="offcanvas-header" id='idHeader'>
        <h5 class="offcanvas-title" id="offcanvasScrollingLabel"></h5>
        <button id='btnClose' type="button" class="btn-close text-reset" aria-label="Close"></button>
    </div>
    <div class="offcanvas-body" id='idBody'>
        <p>Try scrolling the rest of the page to see this option in action.</p>
        <ol class="list-group list-group-numbered">
            <li class="list-group-item d-flex justify-content-between align-items-start">
                <div class="ms-2 me-auto">
                    <div class="fw-bold">Subheading</div>
                    Content for list item
                </div>
                <span class="badge bg-primary rounded-pill">14</span>
            </li>
            <li class="list-group-item d-flex justify-content-between align-items-start">
                <div class="ms-2 me-auto">
                    <div class="fw-bold">Subheading</div>
                    Content for list item
                </div>
                <span class="badge bg-primary rounded-pill">14</span>
            </li>
            <li class="list-group-item d-flex justify-content-between align-items-start">
                <div class="ms-2 me-auto">
                    <div class="fw-bold">Subheading</div>
                    Content for list item
                </div>
                <span class="badge bg-primary rounded-pill">14</span>
            </li>
        </ol>
    </div>
</div>


"""
) {
    private val btnToggle: HTMLElement by this
    private val btnClose: HTMLElement by this
    private val offcanvasScrollingLabel: HTMLElement by this
    private val idHeader: HTMLElement by this
    private val idBody: HTMLElement by this

    var title: String by forward { offcanvasScrollingLabel::innerHTML }
    fun toggle() = btnToggle.click()

    override fun afterRender() {
        setCloseHandlers(btnClose)
        HotkeyWindow.add("META-Escape") { toggle() }
    }

    fun setBody(widget: Widget) {
        idBody.clear()
        idBody.append(widget.containerElement)
    }

    fun setCloseHandlers(element: HTMLElement) {
        element.setAttribute("data-bs-dismiss", "offcanvas")
        element.setAttribute("data-bs-target", "#offcanvasScrolling")
    }

    override fun close() {
        btnClose.click()
    }
}