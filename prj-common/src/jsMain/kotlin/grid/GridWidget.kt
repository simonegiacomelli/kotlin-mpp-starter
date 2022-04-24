package grid

import extensions.*
import kotlinx.dom.addClass
import kotlinx.dom.clear
import org.w3c.dom.HTMLTableElement
import org.w3c.dom.HTMLTableRowElement
import org.w3c.dom.HTMLTableSectionElement
import widget.Widget

open class GridWidget<E>(
    preHtml: String = "", postHtml: String = "",
    createToolbar: (GridWidget<E>) -> MutableList<Widget> = { defaultToolbar(it) }
) : Widget(//language=HTML
    """ $preHtml        
        <table id="table1"></table>
        $postHtml
    """.trimIndent()
) {
    private val table1: HTMLTableElement by this

    companion object {
        var defaultToolbar: (GridWidget<*>) -> MutableList<Widget> = { mutableListOf() }
    }


    val table: HTMLTableElement get() = table1

    var focusedElement: E? = null
        set(value) {
            if (field == value) return
            focusedElementChanged(value)
            field = value
        }
    var focusedElementChangeOnClick = false
    var focusedBackgroundColor = "#f3eded"

    var properties: MutableList<Property<E, *>> = mutableListOf()

    var ordering: Ordering? = null
    var onCustomOrder: (CustomOrderEvent<E>.() -> Unit)? = null

    open var sortableHead: Boolean = true

    var onHeadRender: (PropertyEvent<E>.() -> Unit)? = null
    var onHeadClick: (PropertyEvent<E>.() -> Unit)? = null

    var onDataRender: (ValueEvent<E>.() -> Unit)? = null
    var onDataClick: (ValueEvent<E>.() -> Unit)? = null

    var onElementClick: (ElementEvent<E>.() -> Unit)? = null

    var onProperties: (GridEvent<E>.() -> List<Property<E, *>>)? = null
    protected val propertiesInternal = mutableListOf<Property<E, *>>()

    protected fun refreshPropertiesMapped() {
        val p = onProperties?.invoke(GridEventDc(this)) ?: properties
        propertiesInternal.clear()
        propertiesInternal.addAll(p)
    }

    /** chiamato dagli event-handler interni (e.g., row click) per rilanciare il render
     *  usato dalle extension function che non possono fare l'override di render() */
    var onInternalRender: () -> Unit = { render() }

    protected val elementsInfo = mutableListOf<ElementInfo>()
    protected val elementsInfoMap = mutableMapOf<E, ElementInfo>()

    open var elements: List<E> = listOf()

    val toolbar: MutableList<Widget> by lazy { createToolbar(this) }

    protected fun beforeRender() {
        table.ensureCssStyle()
        refreshPropertiesMapped()
        elementsInfo.clear()
        elementsInfoMap.clear()
    }

    open fun render() = apply {
        beforeRender()
        renderInternal()
    }

    private fun renderInternal() {
        table.tbodyFirst().clear()
        renderHead()
        orderElements(elements).forEachIndexed { elementIndex, element ->
            appendElement(elementIndex, element)
        }
    }

    private val gridEvent = GridEventDc(this)

    protected fun appendElement(elementIndex: Int, element: E) {
        val tr = table1.tbodyFirst().tr()
        if (element == focusedElement) setFocused(tr)

        ElementInfo(elementIndex, element, tr).also {
            elementsInfo.add(it)
            elementsInfoMap[element] = it
        }

        val elementEvent = ElementEventDc(gridEvent, element, elementIndex, tr)
        tr.addEventListener("click", { elementClick(elementEvent) })
        propertiesInternal.forEachIndexed { propertyIndex, property ->
            val value = property.get(element)
            val td = tr.td(value.toStr())
            val propertyEvent = PropertyEventDc(gridEvent, property, propertyIndex, tr, td)
            val valueEvent = ValueEventDc(propertyEvent, elementEvent, value)
            td.addEventListener("click", { onDataClick?.invoke(valueEvent) })
            onDataRender?.invoke(valueEvent)
            property.onDataRender?.invoke(valueEvent)
        }
    }

    private fun elementClick(elementEvent: ElementEventDc<E>) {
        onElementClick?.invoke(elementEvent)
        if (focusedElementChangeOnClick) focusedElement = elementEvent.element
    }

    protected fun renderHead() {
        val head = table1.theadFirst()
        head.clear()

        if (toolbar.isNotEmpty()) appendToolbar(head)

        val tr = head.tr()

        propertiesInternal.forEachIndexed { propertyIndex, property ->
            val th = tr.th(property.caption + ordering.orderingChar(property.name))
            val propertyEvent = PropertyEventDc(gridEvent, property, propertyIndex, tr, th)

            th.onclick = {
                if (sortableHead) ordering = ordering.loop(property.name)
                onHeadClick?.invoke(propertyEvent)
                onInternalRender()
            }

            onHeadRender?.invoke(propertyEvent)
            property.onHeadRender?.invoke(propertyEvent)
        }

    }

    private fun appendToolbar(head: HTMLTableSectionElement) {
        head.tr().th().apply { addClass("toolbar") }.apply {
            toolbar.forEach { tool -> append(tool.container) }
            setAttribute("colspan", "999")
        }
    }

    fun orderElements(elements: List<E>): List<E> {
        val default = defaultComparators()
        val onCustomOrder = onCustomOrder
        val comparators = if (onCustomOrder == null) default
        else {
            val customOrderEvent = CustomOrderEvent(this, default.toMutableList())
            onCustomOrder(customOrderEvent)
            customOrderEvent.comparators
        }

        if (comparators.isEmpty()) return elements

        val comparator = if (comparators.size == 1)
            comparators.first()
        else
            comparators.reduce { a, b -> a.then(b) }

        return elements.sortedWith(comparator)
    }

    fun htmlRowFor(index: Int): HTMLTableRowElement = table1.tbodyFirst().rows()[index]
    fun htmlRowFor(element: E): HTMLTableRowElement = elementInfoFor(element).tr


    fun elementInfoFor(index: Int): ElementInfo = elementsInfo[index]
    fun elementInfoOrNull(element: E): ElementInfo? = elementsInfoMap[element]
    fun elementInfoFor(element: E): ElementInfo = elementInfoOrNull(element) ?: error("Element not found `$element`")

    private fun headRow(index: Int) =
        table.theadFirst().rows().getOrNull(index) ?: error("Header row index=$index is not there")

    fun htmlHeadRow(): HTMLTableRowElement = headRow(1)
    fun htmlToolbarRow(): HTMLTableRowElement = headRow(0)

    private fun defaultComparators(): List<Comparator<E>> {
        val o = ordering ?: return emptyList()
        val property = propertiesInternal.firstOrNull { it.name == o.propertyName } ?: return emptyList()
        val getter = property.get as (E) -> Comparable<Any?>
        val cb: Comparator<E> = compareBy(getter)
        val comparator = if (o.ascending) cb else cb.reversed()
        return listOf(comparator)
    }

    private fun Ordering?.orderingChar(columnName: String): String {
        return if (this == null || columnName != propertyName) "" else if (ascending) "↑" else "↓"
    }

    private fun focusedElementChanged(new: E?) {
        val old = focusedElement
        if (old != null) elementInfoOrNull(old)?.apply { tr.style.removeProperty("background-color") }
        if (new != null) elementInfoOrNull(new)?.apply { setFocused(tr) }
    }

    private fun setFocused(tr: HTMLTableRowElement) {
        tr.style.backgroundColor = focusedBackgroundColor
    }

    protected fun Any?.toStr() = (this ?: "").toString()
}