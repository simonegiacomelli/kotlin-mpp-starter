package extensions

import kotlinx.browser.document
import org.w3c.dom.*

fun Document.br() = createElement("br") as HTMLBRElement
fun Document.table() = createElement("table") as HTMLTableElement
fun Document.div() = createElement("div") as HTMLDivElement
fun Document.button() = createElement("button") as HTMLButtonElement
fun Document.input() = createElement("input") as HTMLInputElement
fun Document.style() = createElement("style") as HTMLStyleElement
fun Document.span() = createElement("span") as HTMLSpanElement
fun Document.script() = createElement("script") as HTMLScriptElement
fun Document.code() = createElement("code") as HTMLElement

fun div() = document.div()
fun br() = document.br()
fun input() = document.input()
fun style() = document.style()
fun span() = document.span()
fun button() = document.button()
fun script() = document.script()
fun code() = document.code()

fun div(html: String) = div().apply { innerHTML = html }
fun div(html: String, builder: HTMLDivElement.() -> Unit) = div(html).apply { builder(this) }
fun input(builder: HTMLInputElement.() -> Unit) = input().apply { builder(this) }
fun code(builder: HTMLElement.() -> Unit) = code().apply { builder(this) }
fun code(html: String) = code().apply { innerHTML = html }
fun span(html: String) = span().apply { innerHTML = html }
fun button(html: String) = button().apply { innerHTML = html }
fun span(html: String, builder: HTMLSpanElement.() -> Unit) = span(html).apply { builder(this) }
