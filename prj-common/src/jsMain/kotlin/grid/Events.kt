package grid

import org.w3c.dom.HTMLTableCellElement
import org.w3c.dom.HTMLTableRowElement

interface GridEvent<E> {
    val grid: GridWidget<E>
}

interface PropertyEvent<E> : GridEvent<E> {
    val property: Property<E, *>
    val propertyIndex: Int
    val tr: HTMLTableRowElement
    val cell: HTMLTableCellElement
}

interface ElementEvent<E> : GridEvent<E> {
    val element: E
    val elementIndex: Int
    val tr: HTMLTableRowElement
}

interface ValueEvent<E> : PropertyEvent<E>, ElementEvent<E> {
    val value: Any?
}

interface LazyLoadEvent<E> : GridEvent<E> {
    val pagination: GridPagination
    var elements: List<E>
    fun loadDone()
}

internal data class GridEventDc<E>(
    override val grid: GridWidget<E>
) : GridEvent<E>

internal data class PropertyEventDc<E>(
    val gridEvent: GridEvent<E>,
    override val property: Property<E, *>,
    override val propertyIndex: Int,
    override val tr: HTMLTableRowElement,
    override val cell: HTMLTableCellElement
) : GridEvent<E> by gridEvent, PropertyEvent<E>

data class ElementEventDc<E>(
    val gridEvent: GridEvent<E>,
    override val element: E,
    override val elementIndex: Int,
    override val tr: HTMLTableRowElement,
) : GridEvent<E> by gridEvent, ElementEvent<E>

internal data class ValueEventDc<E>(
    val propertyEvent: PropertyEvent<E>,
    val elementEvent: ElementEvent<E>,
    override val value: Any?,
    override val grid: GridWidget<E> = propertyEvent.grid,
    override val tr: HTMLTableRowElement = propertyEvent.tr,
) : ValueEvent<E>, PropertyEvent<E> by propertyEvent, ElementEvent<E> by elementEvent


data class LazyLoadEventDc<E>(
    override val grid: GridWidget<E>,
    override val pagination: GridPagination,
    override var elements: List<E> = emptyList(),
    private val loadDoneCallback: (LazyLoadEvent<E>) -> Unit
) : LazyLoadEvent<E> {
    override fun loadDone() {
        loadDoneCallback(this)
    }
}

data class CustomOrderEvent<E>(
    override val grid: GridWidget<E>,
    var comparators: MutableList<Comparator<E>>
) : GridEvent<E>