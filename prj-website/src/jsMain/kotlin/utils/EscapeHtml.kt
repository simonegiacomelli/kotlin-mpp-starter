package utils

import kotlinx.browser.document
import org.w3c.dom.HTMLTextAreaElement

private val escape = document.createElement("textarea") as HTMLTextAreaElement

val String.escapeHTML: String
    get() {
        escape.textContent = this
        return escape.innerHTML
    }

val String.unescapeHTML: String
    get() {
        escape.innerHTML = this
        return escape.textContent ?: ""
    }