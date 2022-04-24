package extensions

import org.w3c.dom.HTMLElement
import org.w3c.dom.NodeList
import org.w3c.dom.asList

var HTMLElement.extVisible: Boolean
    get() = style.display != "none"
    set(newVisible) {
        if (newVisible) extShow() else extHide()
    }

fun HTMLElement.extShow() {
    if (extVisible) return
    val saved = this.asDynamic()["ext_save_display_style"]
    style.display = if (saved is String) saved else ""
    if (style.length == 0) attributes.removeNamedItem("style") // removeAttribute("style") has a strange behaviour
}

fun HTMLElement.extHide() {
    if (!extVisible) return
    this.asDynamic()["ext_save_display_style"] = style.display
    style.display = "none"
}

var NodeList.extVisible: Boolean
    get() = asList().filterIsInstance<HTMLElement>().all { it.extVisible }
    set(value) {
        asList().filterIsInstance<HTMLElement>().forEach { it.extVisible = value }
    }
