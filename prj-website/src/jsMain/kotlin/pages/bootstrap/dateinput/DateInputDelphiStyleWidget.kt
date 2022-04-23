package pages.bootstrap.dateinput

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.KeyboardEvent
import pages.bootstrap.commonwidgets.InputGroupWidget
import widget.Widget

class DateInputDelphiStyleWidget : Widget(//language=HTML
    """
<div id='id1'></div>
<textarea id='idt' rows='30'></textarea>
"""
) {
    private val id1 by this { InputGroupWidget() }
    private val idt: HTMLTextAreaElement by this

    override fun afterRender() {
        id1.input.setupDateInsert()
        id1.input.onkeydown = {
            idt.value = "${it.key}\n" + idt.value
            0
        }
        idt.style.fontSize = "x-small"
        idt.rows = 30
    }

}

fun HTMLInputElement.setupDateInsert() = HTMLInputDateModify(this)

class HTMLInputDateModify(private val input: HTMLInputElement) {
    private var oldValue: String = ""
    private var oldIndex: Int = 0

    private fun HTMLInputElement.saveState() {
        oldValue = value
        oldIndex = blockCaretIndex
    }

    private fun HTMLInputElement.restoreState() {
        value = oldValue
        blockCaretIndex = oldIndex
    }

    init {
        with(input) {
            value = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
            blockCaretIndex = 0
            saveState()
//            type = "date" not good because the browser changes it's behaviour
            input.style.fontFamily = "monospace"
            oninput = { doInput() }
            addEventListener("beforeinput", { saveState() })
            addEventListener("keydown", { if (it is KeyboardEvent) onKeydown(it) })


            onfocus = { selectionLength = 1; 0 }
        }
    }

    private fun onKeydown(event: KeyboardEvent) = when (event.key) {
        "ArrowLeft" -> run { moveSelection(-1); event.preventDefault() }
        "ArrowRight" -> run { moveSelection(+1); event.preventDefault() }
        else -> {}
    }

    private fun doInput(): Unit = input.run {
        if (!isValidDate())
            return restoreState()


        if (selectionStartIndex == value.length) return
        moveNextIfNotDigit()
        selectionLength = 1
    }

    private fun HTMLInputElement.isValidDate(): Boolean = runCatching { value.toLocalDate() }.isSuccess

    private fun HTMLInputElement.moveNextIfNotDigit() {
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