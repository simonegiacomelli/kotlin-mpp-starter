package grid

import extensions.*
import kotlinx.dom.addClass
import kotlinx.dom.clear
import org.w3c.dom.HTMLTableElement
import org.w3c.dom.HTMLTableRowElement
import org.w3c.dom.HTMLTableSectionElement
import widget.Widget
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType

open class GridWidget<E>(
    preHtml: String = "", postHtml: String = "",
    init: (GridWidget<E>) -> Unit = { defaultInit(it) },
    createToolbar: (GridWidget<E>) -> MutableList<Widget> = { defaultToolbar(it) }
) : Widget(//language=HTML
    """ $preHtml        
        <table id="table1"></table>
        $postHtml
    """.trimIndent()
) {
    private val table1: HTMLTableElement by this

    companion object {
        var defaultInit: (GridWidget<*>) -> Unit = {}
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
    var onCustomOrder: (CustomOrderEvent<E>.() -> Unit)? by propDele()

    open var sortableHead: Boolean = true

    var onHeadRender: (PropertyEvent<E>.() -> Unit)? by propDele()
    var onHeadClick: (PropertyEvent<E>.() -> Unit)? by propDele()

    var onDataRender: (ValueEvent<E>.() -> Unit)? by propDele()
    var onDataClick: (ValueEvent<E>.() -> Unit)? by propDele()

    var onElementClick: (ElementEvent<E>.() -> Unit)? by propDele()
    var onElementRender: (ElementEvent<E>.() -> Unit)? by propDele()

    protected val propertiesInternal = mutableListOf<Property<E, *>>()

    protected fun refreshPropertiesMapped() {
        propertiesInternal.clear()
        propertiesInternal.addAll(properties)

        val event: MapPropertiesEvent<E> = object : MapPropertiesEvent<E> {
            override val properties: MutableList<Property<E, *>> get() = propertiesInternal
            override val grid: GridWidget<E> get() = this@GridWidget
        }

        notifyEvent(event)
    }

    val observers = mutableMapOf<KClass<*>, MutableSet<GridObserver<E, *>>>()


    inline fun <reified Ev : GridEvent<E>, reified Ha : (Ev) -> Unit> propDele(): ReadWriteProperty<Any?, Ha?> {

        return object : ReadWriteProperty<Any?, Ha?> {
            var backing: Ha? = null
            var observer: GridObserver<E, Ev>? = null

            override fun getValue(thisRef: Any?, property: KProperty<*>): Ha? = backing

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: Ha?) {
                backing = value
                observer?.let { observersFor<Ev>().remove(it) }
                observer = null
                if (value != null) {
                    val element = GridObserver<E, Ev> { event -> value(event) }
                    observer = element
                    addObserver(element)
                }
            }
        }
    }

    protected val elementsInfo = mutableListOf<ElementInfo>()
    protected val elementsInfoMap = mutableMapOf<E, ElementInfo>()

    open var elements: List<E> = listOf()

    val toolbar: MutableList<Widget> by lazy { createToolbar(this) }
    private val initOnce by lazy { init(this) }

    protected fun beforeRender() {
        table.ensureCssStyle()
        elementsInfo.clear()
        elementsInfoMap.clear()
        refreshPropertiesMapped()
    }

    open fun render() = apply {
        initOnce
        beforeRender()
        renderInternal()
    }

    private fun renderInternal() {
        table.tbodyFirst().clear()
        renderHead()
        orderElements(elements).forEachIndexed { elementIndex, element ->
            appendElement(elementIndex, element)
        }
        val event: AfterRenderEvent<E> = object : AfterRenderEvent<E>, GridEvent<E> by gridEvent {}
        notifyEvent(event)
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
        onElementRender?.invoke(elementEvent)
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

    private fun elementClick(elementEvent: ElementEvent<E>) {
        onElementClick?.invoke(elementEvent)
        if (focusedElementChangeOnClick) focusedElement = elementEvent.element
    }

    protected fun renderHead() {
        val head = table1.theadFirst()
        head.clear()

        appendToolbar(head)

        val tr = head.tr()

        propertiesInternal.forEachIndexed { propertyIndex, property ->
            val th = tr.th(property.caption + ordering.orderingChar(property.name))
            val propertyEvent = PropertyEventDc(gridEvent, property, propertyIndex, tr, th)

            th.onclick = {
                if (sortableHead) ordering = ordering.loop(property.name)
                onHeadClick?.invoke(propertyEvent)
                render()
            }

            onHeadRender?.invoke(propertyEvent)
            property.onHeadRender?.invoke(propertyEvent)
        }

    }

    private fun appendToolbar(head: HTMLTableSectionElement) {
        val tr = head.tr()
        if (toolbar.isEmpty()) return
        tr.th().apply { addClass("toolbar") }.apply {
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


    inline fun <reified Ev : GridEvent<E>> addObserver(crossinline eventHandler: (event: Ev) -> Unit): GridObserver<E, Ev> {
        return addObserver(GridObserver { event -> eventHandler(event) })

    }

    inline fun <reified Ev : GridEvent<E>> addObserver(element: GridObserver<E, Ev>): GridObserver<E, Ev> {
        observersFor<Ev>().add(element)
        return element
    }

    inline fun <reified Ev : GridEvent<E>> observersFor(): MutableSet<GridObserver<E, Ev>> {
        val orPut = observers.getOrPut(Ev::class) { mutableSetOf() } as MutableSet<GridObserver<E, Ev>>
        return orPut
    }

    /** public because (in principle) inheritors can use observers/notify infrastructure; e.g., LazyGridWidget? */
    inline fun <reified Ev : GridEvent<E>> notifyEvent(event: Ev) {
//        println("nameOf=${nameOf(Ev::class)}")
        observersFor<Ev>().forEach { it.notify(event) }
//        map[Ev::class]?.also {
//            it.filterIsInstance<GridObserver<E, Ev>>().forEach {
//                it.notify(event)
//            }
//        }
//
//        h.forEach {
//            // it seems filterIsInstance is not filtering as I would expect
//            // because it raises class cast exception anyway
//            kotlin.runCatching { it.notify(event) }
//        }
    }
}

fun KType.print() {
    println("classifier=$classifier")
    println("isMarkedNullable=$isMarkedNullable")
    println("classifier=${arguments.joinToString("\n") { "  $it" }}")
}
//
//fun <E, Ev : GridEvent<E>> MutableList<GridObserver<E, *>>.add(block: (Ev) -> Unit) =
//    add(GridObserver<E, Ev> { event -> block(event) })
//


fun interface GridObserver<E, Ev : GridEvent<E>> {
    fun notify(event: Ev)
}

interface MapPropertiesEvent<E> : GridEvent<E> {
    val properties: MutableList<Property<E, *>>
}

interface AfterRenderEvent<E> : GridEvent<E>