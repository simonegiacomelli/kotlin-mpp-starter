package pages.bootstrap

import org.w3c.dom.HTMLElement
import utils.forward
import widget.Widget


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