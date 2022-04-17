package widget

import kotlinx.browser.document
import org.w3c.dom.*
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

open class Widget(val html: String) {
    var htmlTemplate = false

    constructor(template: HTMLTemplateElement) : this("") {
        htmlTemplate = true
        val array: Array<Node> = document.importNode(template.content, true).childNodes.asList().toTypedArray()
        explicitContainer = document.createElement("div")
        explicitContainer.append(*array)
    }

    fun log(msg: String) = console.log("$msg\n") // helper for console output running task jsTest
    var namedDescendant: Map<String, Widget> = emptyMap()
    lateinit var widgetFactory: WidgetFactory
    lateinit var holderWidget: HolderWidget
    lateinit var explicitContainer: Element

    private val expandOnce = OnlyOnce { expandContainer(); }

    val container: Element get() = computeContainer()

    private fun computeContainer(): Element {
        expandOnce.invoke(); return explicitContainer
    }

    private fun expandContainer() {
        // verifico se il container e' stato specificato esternamente
        if (::explicitContainer.isInitialized)
            params.elements.addAll(explicitContainer.children.asList())
        else
            explicitContainer = document.createElement("div")
        val container = explicitContainer
        container.setAttribute("w-type", this::class.simpleName ?: "")
        if (!htmlTemplate)
            container.innerHTML = html
        expandContainedWidgetIfAny(container)
        afterRender()
        afterRenderCallback()
    }

    private fun expandContainedWidgetIfAny(element: Element) {
        val widgetToExpand = element.collectWidgetToExpand()
        val (toExpandWidgetFactory, toExpandInvokeWidget) = widgetToExpand.partition {
            !invokeWidgets.containsKey(it.id)
        }
        toExpandInvokeWidget.forEach {
            // expand widgets registered with invoke()
            val widget = invokeWidgets[it.id]!!.value
            if (this::holderWidget.isInitialized)
                widget.holderWidget = holderWidget
            widget.container //force expand
        }

        if (toExpandWidgetFactory.isNotEmpty())
            expandWidgetFactory(toExpandWidgetFactory)
    }

    private fun expandWidgetFactory(toExpandWidgetFactory: List<Element>) {
        if (!::widgetFactory.isInitialized)
            throw MissingWidgetFactory(
                "These widget needs to be expanded:" +
                        " [${toExpandWidgetFactory.joinToString { it.tagName }}] but no ${WidgetFactory::class.simpleName} is available to this widget having html: [$html] "
            )
        namedDescendant = toExpandWidgetFactory.map {
            val widget = widgetFactory.new(it.tagName)
            widget.explicitContainer = it
            widget.container //force expand
            widget
        }.filter { it.container.id.isNotBlank() }.associateBy({ it.container.id }, { it })
    }

    private fun Element.collectWidgetToExpand(): List<Element> {
        val dict = children.asList().groupBy {
            it.tagName.uppercase().startsWith("W-") || accept(it)
        }
        val toExpand = dict[true] ?: emptyList()
        val toScan = dict[false] ?: emptyList()
        val nestedToExpand = toScan.flatMap { it.collectWidgetToExpand() }
        return toExpand + nestedToExpand
    }

    private fun accept(it: Element): Boolean {
        val id = it.id
        val ok = invokeWidgets.containsKey(id)
        return ok
    }

    fun getElement(property: KProperty<*>): Element = container.querySelector("#${property.name}")
        ?: throw ElementNotFound("Name: [${property.name}] html: [$html]")

    inline operator fun <reified T> getValue(thisRef: Any?, property: KProperty<*>): T {
        container // force expand to collect descendants
        val widget = this.namedDescendant[property.name]

        if (widget != null && T::class.isInstance(widget)) {
            return widget as T
        }
        val element = getElement(property)
        if (!T::class.isInstance(element))
            throw ClassCastException(
                "Element instance is of type ${element::class.js.name}" +
                        " but delegate is of type ${(T::class as KClass<out Element>).js.name}. html: [$html]"
            )

        return element as T
    }

    val params = Params()

    class Params {
        val elements = mutableListOf<Element>()
        inline operator fun <reified T> getValue(thisRef: Any?, property: KProperty<*>): T? {
            return elements.find { it.id == property.name } as T?
        }
    }

    open fun close() {
        checkedHolderWidget.close(this)
    }

    open fun show(widget: Widget) {
        checkedHolderWidget.show(widget)
    }

    private val checkedHolderWidget: HolderWidget
        get() {
            if (!::holderWidget.isInitialized)
                throw MissingHolderWidget("html: [$html]")
            return holderWidget
        }
    var afterRenderCallback: Widget.() -> Unit = { }
    open fun afterRender() {}
    val invokeWidgets = mutableMapOf<String, Lazy<Widget>>()

    inline operator fun <reified T : Widget> invoke(crossinline function: () -> T): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> {
        return PropertyDelegateProvider { _: Any?, property ->
            val instance = lazy { function().also { it.explicitContainer = getElement(property) } }
            invokeWidgets[property.name] = instance
            ReadOnlyProperty<Any?, T> { _, _ -> container; instance.value }
        }
    }


}

fun <T : Widget> T.afterRender(lambda: T.() -> Unit): T {
    afterRenderCallback = lambda as Widget.() -> Unit
    return this
}

class ElementNotFound(msg: String) : Throwable(msg)
class MissingWidgetFactory(msg: String) : Throwable(msg)
class MissingHolderWidget(msg: String) : Throwable(msg)

class WidgetFactory {
    private val list = mutableMapOf<String, () -> Widget>()
    fun register(name: String, function: () -> Widget) {
        list[name.uppercase()] = function
    }

    fun new(name: String): Widget {
        return list[name.uppercase()]!!()
    }
}

open class HolderWidget(html: String = "") : Widget(html) {
    val stack = mutableListOf<Widget>()

    open val containerProvider: Element get() = container

    override fun show(widget: Widget) {
        widget.holderWidget = this
        stack.add(widget)
        containerProvider.children[0]?.remove()
        containerProvider.appendChild(widget.container)
    }

    open fun closeCurrent() {
        if (stack.size <= 1)
            return
        stack.removeLast()
        show(stack.removeLast())
    }

    fun clear() {
        stack.clear()
        containerProvider.children[0]?.remove()
    }

    fun close(widget: Widget) {
        if (stack.isEmpty() || widget != stack.last())
            return
        closeCurrent()
    }

}

class OnlyOnce(val function: () -> Unit) {
    var called = false
    fun invoke() {
        if (called) return
        called = true
        function()
    }
}

val Widget.containerElement: HTMLElement get() = container as HTMLElement