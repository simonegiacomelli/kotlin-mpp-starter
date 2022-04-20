package pages.bootstrap.dateinput

import keyboard.Hotkey
import kotlinx.browser.window
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.KeyboardEvent
import pages.bootstrap.commonwidgets.InputGroupWidget
import widget.Widget

class DateInputDelphiStyleWidget : Widget(//language=HTML
    """
<div id='id1'></div>    
"""
) {
    private val id1 by this { InputGroupWidget() }

    override fun afterRender() {
        id1.input.setupDateInsert()
    }

}

fun HTMLInputElement.setupDateInsert() = HTMLInputDateModify(this)

class HTMLInputDateModify(private val input: HTMLInputElement) {

    init {
        with(input) {
            value = "18/01/2022"
//            type = "date" not good because the browser changes it's behaviour
            input.style.fontFamily = "monospace"
            onkeypress = { doKeypress(it) }
            Hotkey(this).add("ArrowLeft") { moveSelection(-1); it.preventDefault() }
            Hotkey(this).add("ArrowRight") { moveSelection(+1); it.preventDefault() }

        }
    }

    private fun doKeypress(it: KeyboardEvent): Unit = input.run {
        if (selectionStartIndex == value.length) {
            it.preventDefault()
            return
        }
        moveNextIfDigit()
        selectionLength = 1
        // let the char into the value buffer
        window.setTimeout({ selectionLength = 1; moveNextIfDigit(); }, 1)
    }

    private fun HTMLInputElement.moveNextIfDigit() {
        if (!currentChar.isDigit()) moveSelection(+1)
    }

    private fun moveSelection(offset: Int): Unit = input.run {
        val newIndex = selectionStartIndex + offset
        if (newIndex !in value.indices) {
            println("$newIndex not in ${value.indices}")
            return
        }
        blockCaretIndex = newIndex
        if (!currentChar.isDigit()) {
            println("recursing!")
            moveSelection(offset)
        }
    }

}

private val HTMLInputElement.currentChar get() = value[selectionStartIndex]
private var HTMLInputElement.blockCaretIndex: Int
    get() = selectionStart ?: 0
    set(value) {
        selectionStart = value
        selectionEnd = value + 1
    }
private var HTMLInputElement.selectionStartIndex: Int
    get() = selectionStart ?: 0
    set(value) = run { selectionStart = value }
private var HTMLInputElement.selectionEndIndex: Int
    get() = selectionEnd ?: 0
    set(value) = run { selectionEnd = value }

private var HTMLInputElement.selectionLength: Int
    get() = selectionEndIndex - (selectionStart ?: 0)
    set(value) = run { selectionEndIndex = selectionStartIndex + value }