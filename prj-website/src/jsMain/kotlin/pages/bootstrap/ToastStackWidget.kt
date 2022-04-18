package pages.bootstrap

import kotlinx.dom.addClass
import widget.Widget
import widget.containerElement

class ToastStackWidget : Widget("") {

    override fun afterRender() {
        containerElement.addClass(*"toast-container position-fixed bottom-0 end-0 p-3".split(" ").toTypedArray())
        containerElement.style.zIndex = "2000"
    }

    fun showToast(toast: ToastWidget) {
        container.append(toast.container)
        toast.show()
    }

    fun showToast(message: String) {
        showToast(ToastWidget().apply { body = message })
    }
}