package extensions

import kotlinx.browser.document
import org.w3c.dom.*

/*
 HTML strangeness:

 table.createTHead() se thead esistesse gia', restituisce quello. se non esiste lo crea, aggiunge e lo restitiusce
 table.createTbody() ne crea sempre uno nuovo, lo appende e lo restituisce
 */

/**
 * questo e' un memento per sottolineare l'incongruenza tra il nome del metodo e il comportamento.
 */
val HTMLTableElement.tHeadFirstExt get() = this.createTHead()

val HTMLTableElement.tBodyFirstExt
    get() = (this.tBodies.asList().firstOrNull() ?: this.createTBody()) as HTMLTableSectionElement

/**
 * da specifiche non si potrebbe ma funziona sia in Chrome che in Safari
 */
fun HTMLTableElement.thead() = (document.createElement("thead") as HTMLTableSectionElement).also { append(it) }
fun HTMLTableElement.tbody() = (document.createElement("tbody") as HTMLTableSectionElement).also { append(it) }
fun HTMLTableElement.theadFirst() = tHeadFirstExt
fun HTMLTableElement.tbodyFirst() = tBodyFirstExt

fun HTMLTableSectionElement.tr() = this.tr(-1)
fun HTMLTableSectionElement.tr(index: Int) = this.insertRow(index) as HTMLTableRowElement
val HTMLTableSectionElement.rowsExt: List<HTMLTableRowElement>
    get() = this.rows.asList().filterIsInstance<HTMLTableRowElement>()

operator fun HTMLTableSectionElement.get(rowIndex: Int): HTMLTableRowElement {
    val element = rows[rowIndex] ?: error("Row not found at index: $rowIndex")
    val row = element as HTMLTableRowElement
    return row
}

operator fun HTMLTableRowElement.get(cellIndex: Int): HTMLTableCellElement {
    val element = cells[cellIndex] ?: error("Cell not found at index: $cellIndex")
    val cell = element as HTMLTableCellElement
    return cell
}

operator fun HTMLTableSectionElement.get(rowIndex: Int, cellIndex: Int): HTMLTableCellElement {
    val cell = this[rowIndex][cellIndex]
    return cell
}

fun HTMLTableSectionElement.rows() = rowsExt
fun HTMLTableRowElement.cells() = this.cells.asList().filterIsInstance<HTMLTableCellElement>()

fun HTMLTableRowElement.th() = (document.createElement("th") as HTMLTableCellElement).also { append(it) }
fun HTMLTableRowElement.td() = this.insertCell() as HTMLTableCellElement

fun HTMLTableSectionElement.deleteAllRows() = run { while (childElementCount > 0) deleteRow(0) }

fun <T> HTMLTableRowElement.td(function: HTMLTableCellElement.() -> T) = td().also { function(it) }
fun <T> HTMLTableRowElement.th(function: HTMLTableCellElement.() -> T) = th().also { function(it) }
fun <T> HTMLTableSectionElement.tr(function: HTMLTableRowElement.() -> T) = tr().also { function(it) }

fun HTMLTableRowElement.td(html: String) = td().apply { innerHTML = html }
fun HTMLTableRowElement.th(html: String) = th().apply { innerHTML = html }
