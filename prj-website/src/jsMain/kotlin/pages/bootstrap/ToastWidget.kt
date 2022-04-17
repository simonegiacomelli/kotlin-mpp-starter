package pages.bootstrap

import extensions.div
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import utils.forward
import widget.Widget

private val toastStacking = div().apply {
    innerHTML = //language=HTML
        """<div class="toast-container position-fixed bottom-0 end-0 p-3" style="z-index: 2000">"""
}.firstElementChild!!

class ToastWidget : Widget(//language=HTML
    """
<div id="liveToast" class="toast hide" role="alert" aria-live="assertive" aria-atomic="true">
    <div class="toast-header">
        <img src="" class="rounded me-2" alt="">
        <strong class="me-auto">Information</strong>
        <small>0 secs ago</small>
        <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
    
    <div class="toast-body" id="idBody">
    
    </div>
</div>
"""
) {
    private val idBody: HTMLElement by this
    private val liveToast: HTMLElement by this
    var body: String by forward { idBody::innerHTML }

    private val bsToast: dynamic by lazy {
        val lt = liveToast
        js("bootstrap.Toast.getOrCreateInstance(lt)");
    }

    fun show() {
        document.body?.append(toastStacking)
        toastStacking.append(container)
        bsToast.show()
        liveToast.addEventListener("hidden.bs.toast", {
            container.remove()
        })
    }

    companion object {
        fun show(message: String) {
            ToastWidget().apply { body = message }.show()
        }
    }
}