package pages.bootstrap.dateinput

import kotlinx.datetime.*
import kotlinx.datetime.DateTimeUnit.Companion.DAY
import kotlinx.datetime.DateTimeUnit.Companion.MONTH
import kotlinx.datetime.DateTimeUnit.Companion.YEAR
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

    private fun HTMLInputElement.onKeydown(event: KeyboardEvent) = when (event.key) {
        "ArrowLeft" -> run { moveSelection(-1); true }
        "ArrowRight" -> run { moveSelection(+1); true }
        "ArrowUp" -> run { addDatePart(+1); true }
        "ArrowDown" -> run { addDatePart(-1); true }
        else -> run { false }
    }.let {
        if (it) event.preventDefault() else {
            if (selectionLength > 1) {
                val (range, unit) = currentRangeDateTimeUnit() ?: return
                blockCaretIndex = range.first
            }
        }
    }

    private fun HTMLInputElement.keepSelection(block: HTMLInputElement.() -> Unit) {
        val start = selectionStartIndex
        val end = selectionEndIndex
        block()
        selectionStartIndex = start
        selectionEndIndex = end
    }

    private val unitOrder = listOf(YEAR, MONTH, DAY)
    private val rangeToUnit = mapOf(
        (0..3) to YEAR,
        (5..6) to MONTH,
        (8..9) to DAY,
    )
    private val unitToRange = rangeToUnit.entries.associate { (k, v) -> v to k }

    private fun HTMLInputElement.addDatePart(amount: Int) {

        val date = dateOrNull() ?: return

        val (range, unit) = currentRangeDateTimeUnit() ?: return
        value = date.plus(amount, unit).toString()
        selectRange(range)
        saveState()
    }

    private fun HTMLInputElement.selectRange(unit: DateTimeUnit) {
        val range = unitToRange[unit] ?: return
        selectRange(range)
    }

    private fun HTMLInputElement.selectRange(range: IntRange) {
        selectionStartIndex = range.first
        selectionEndIndex = range.last + 1
    }

    private fun HTMLInputElement.currentRangeDateTimeUnit(): Pair<IntRange, DateTimeUnit.DateBased>? {
        val range = rangeToUnit.keys.firstOrNull { selectionStart in it } ?: return null
        val unit = rangeToUnit[range] ?: return null
        return Pair(range, unit)
    }

    private fun doInput(): Unit = input.run {
        if (!isValidDate())
            return restoreState()


        if (selectionStartIndex == value.length) return
        moveNextIfNotDigit()
        selectionLength = 1
    }

    private fun HTMLInputElement.isValidDate(): Boolean = dateOrNull() != null
    private fun HTMLInputElement.dateOrNull(): LocalDate? = runCatching { value.toLocalDate() }.getOrNull()

    private fun HTMLInputElement.moveNextIfNotDigit() {
        if (!currentChar.isDigit()) moveSelection(+1)
    }

    private fun moveSelection(offset: Int): Unit = input.run {
        if (selectionLength > 1) return moveSelectionBlock(offset)

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

    private fun moveSelectionBlock(offset: Int): Unit = input.run {
        val (range, unit) = currentRangeDateTimeUnit() ?: return
        val nextUnit = unitOrder.elementAtOrNull(unitOrder.indexOf(unit) + offset) ?: return
        selectRange(nextUnit)
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