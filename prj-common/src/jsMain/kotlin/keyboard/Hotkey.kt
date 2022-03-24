package keyboard

import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.KeyboardEvent

val HotkeyWindow by lazy { Hotkey(window) }

fun Hotkey(element: EventTarget, log_prefix: String? = null) =
    Hotkey({ element.addEventListener("keydown", it) }, log_prefix)

class Hotkey(
    addKeydownHandler: ((Event) -> Unit) -> Unit,
    var log_prefix: String? = null,
) {
    private val handlers = mutableMapOf<String, (KeyboardEvent) -> Unit>()

    init {
        addKeydownHandler(::on_keydown)
    }

    // see https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent/key/Key_Values
    /**
     * See https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent/key/Key_Values
     */
    fun add(vararg hotkey: String, callback: (KeyboardEvent) -> Unit): Hotkey {
        hotkey.forEach { handlers[it] = callback }
        return this
    }

    private fun on_keydown(event: Event) {
        if (event !is KeyboardEvent) return
        var key = ""
        if (event.ctrlKey) key += "CTRL-"
        if (event.shiftKey) key += "SHIFT-"
        if (event.altKey) key += "ALT-"
        if (event.metaKey) key += "META-"
        key += event.key.run { if (length == 1) uppercase() else this }
        val handle = handlers[key]
        if (log_prefix != null) println("$log_prefix $key" + if (handle != null) " (handler found)" else "")
        handle?.invoke(event)
    }
}
