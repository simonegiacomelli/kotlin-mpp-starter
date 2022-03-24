package widget

import kotlinx.browser.document
import org.w3c.dom.*
import utils.assert
import kotlin.test.*

class WidgetTest {

    @Test
    fun simpleHtml_shouldBePresentInContainer() {
        val target = Widget("<span>hi joe</span>")
        assert(target.container.innerHTML).contains("hi joe")
    }

    @Test
    fun withNestedWidget_withoutWidgetFactory_shouldComplaintAboutImpossibilityToExpandNestedWidget() {
        val target = Widget("<w-Widget1></w-Widget1>")
        val exception = assertFailsWith<MissingWidgetFactory> { target.container }
        assert(exception.message ?: "").contains(target.html)
    }

    @Test
    fun aWidget_withSpecifiedContainer_shouldUseItAsContainerElement() {
        val target = Widget("<button>click me</button>")
        target.explicitContainer = document.createElement("span")
        assert(target.container.outerHTML).contains("<span", "</span>", "click me")
    }

    @Test
    fun shouldProvideDelegateToAccessElements() {
        val target = twoDivs()
        val div3: HTMLElement by target
        assertEquals("baz", div3.innerHTML)
    }

    @Test
    fun withNonExistingElement_shouldThrowException() {
        val target = twoDivs()
        assertFailsWith<ElementNotFound> {
            val divDoNotExist: HTMLElement by target
            val s = divDoNotExist.id
        }
    }

    @Test
    fun wType_onContainer() {
        //no defined class
        val target1 = Widget("<button>foo</button>")
        assertEquals(Widget::class.simpleName, target1.containerElement.getAttribute("w-type"))
        //defined class
        class CustomWidget : Widget("<button>foo</button>")

        val target = CustomWidget()
        assertEquals(CustomWidget::class.simpleName, target.containerElement.getAttribute("w-type"))
    }

    @Test
    fun shouldProvideDelegateToAccessWidget() {

        class Widget1 : Widget("""<span id="span1">original</span>""") {
            val span1: HTMLSpanElement by this
        }

        val target = Widget("""<w-widget1 id="widget1"></w-widget1>""")
        target.widgetFactory = WidgetFactory().apply { register("w-widget1") { Widget1() } }

        fun testWidget1Type() {
            val widget1: Widget1 by target
            assert(widget1.span1.innerHTML).contains("original")
        }

        fun testWidgetBaseType() {
            val widget1: Widget by target
            assertTrue(Widget1::class.isInstance(widget1))
        }

        fun testHtmlType() {
            val widget1: HTMLElement by target
            assert(widget1.id).contains("widget1")
        }
        testWidget1Type()
        testWidgetBaseType()
        testHtmlType()
    }

    @Test
    fun shouldBeAbleToCloseItself() {
        val holderWidget = HolderWidget()

        holderWidget.show(twoDivs())
        val widget1 = oneDiv()
        val widget2 = oneDiv()
        holderWidget.show(widget1)
        holderWidget.show(widget2)

        widget2.close()
        widget1.close()

        assert(holderWidget.container.innerHTML).contains(twoDivsMarkers)
    }

    @Test
    fun whenNotShown_CloseShouldHaveNoEffect() {
        val holderWidget = HolderWidget()

        val widget1 = oneDiv()
        holderWidget.show(widget1)
        holderWidget.show(twoDivs())

        widget1.close()

        assert(holderWidget.container.innerHTML).contains(twoDivsMarkers)
    }

    @Test
    fun aWidgetShownWithWidgetHolder_canShowOtherWidgets() {
        val holderWidget = HolderWidget()

        val widget1 = oneDiv()
        val widget2 = twoDivs()

        holderWidget.show(widget1)
        widget1.show(widget2)

        assert(holderWidget.container.innerHTML).contains(twoDivsMarkers)
        assert(holderWidget.container.innerHTML).not.contains(oneDivMarkers)
    }

    @Test
    fun aWidgetWithoutWidgetHolder_thatTriesToShowOrCloseAnotherWidget_shouldTrow() {
        val widget1 = oneDiv()
        val widget2 = twoDivs()

        val exShow = assertFailsWith<MissingHolderWidget> { widget1.show(widget2) }
        val exClose = assertFailsWith<MissingHolderWidget> { widget1.close() }

        listOf(exShow, exClose)
            .map { it.message ?: "" }
            .forEach { assert(it).contains(widget1.html) }
    }

    @Test
    fun canUseAfterRenderMethodAndLambda_AndUseDefinedElements() {
        var callCount = 0
        var methodCalled = false
        var lambdaCalled = false

        class Widget1 : Widget("""<button id="button">click me</button>""") {
            val button: HTMLButtonElement by this
            override fun afterRender() {
                methodCalled = true
                callCount++
                button.onclick = { }
            }
        }

        val target = Widget1()
        target.afterRender {
            lambdaCalled = true
            callCount++
            button.onclick = { }
        }
        assertEquals(0, callCount)
        target.container
        assertTrue(lambdaCalled)
        assertTrue(methodCalled)
        assertEquals(2, callCount)
    }

    @Test
    fun afterRenderShouldBeAbleToAccess_container() {
        val widget = Widget("""<button id="button">click me</button>""")
        widget.afterRender { container }
        widget.container
    }

    @Test
    fun namelessParams_PassedByWidget() {
        class Widget1 : Widget("""<button id="btn1">click me</button>""")

        val wf = WidgetFactory().apply { register("w-widget1") { Widget1() } }
        Widget("""<w-Widget1 id="w1"><div>hello</div><div>joe</div></w-Widget1>""").apply {
            widgetFactory = wf
            val w1: Widget1 by this
            val elements = w1.params.elements
            assertEquals(2, elements.size)
            assert(elements[0].innerHTML).contains("hello")
            assert(elements[1].innerHTML).contains("joe")
        }

    }

    @Test
    fun namedParams_PassedByWidget() {
        class Widget1 : Widget("""<button id="btn1">click me</button>""") {
            val div1: HTMLDivElement? by params
            val btn1: HTMLButtonElement by this
        }

        val wf = WidgetFactory().apply { register("w-widget1") { Widget1() } }
        Widget("""<w-Widget1 id="w1"><div id="div1">hello</div></w-Widget1>""").apply {
            widgetFactory = wf
            val w1: Widget1 by this
            assertNotNull(w1.div1)
            assert(w1.div1?.innerHTML ?: "").contains("hello")
            assert(w1.btn1.innerHTML).contains("click me")
        }

    }

    @Test
    fun classCastException_whenElement() {
        val target = Widget("""<button id="btn1"></button>""")
        val btn1: HTMLSpanElement by target
        val exception = assertFailsWith<ClassCastException> { btn1.innerHTML }
        val spanClassName = HTMLSpanElement::class.js.name
        val buttonClassName = HTMLButtonElement::class.js.name
        assert(exception.message ?: "").contains(spanClassName, buttonClassName)
    }

    @Test
    fun classCastException_whenAnotherTypeEntirely() {
        val target = Widget("""<button id="btn1"></button>""")
        val btn1: String by target
        val exception = assertFailsWith<ClassCastException> { btn1.length }
        val buttonClassName = HTMLButtonElement::class.js.name
        assert(exception.message ?: "").contains("String", buttonClassName)
    }

    @Test
    fun template() {
        val target =
            Widget("""<table id="table1"></table><template id="template1"><tr id="tr1"><td id="td1"></td></tr></template>""")

        val table1: HTMLTableElement by target
        val template1: HTMLTemplateElement by target

        class Template1 : Widget(template1) {
            val tr1: HTMLTableRowElement by this
            val td1: HTMLTableCellElement by this
        }

        table1.createTBody()
        val t1 = Template1().also { it.td1.innerHTML = "foo" }
        val t2 = Template1().also { it.td1.innerHTML = "bar" }
        assert(target.container.innerHTML).not.contains("foo", "bar")
        println(target.container.innerHTML)
        table1.tBodies[0]!!.append(t1.tr1)
        table1.tBodies[0]!!.append(t2.tr1)
        println(target.container.innerHTML)
        assert(target.container.innerHTML).contains("foo", "bar")
    }

}

class WidgetWithGenericsTest {
    companion object {
        val html = """<div id="idDiv"></div>"""
    }

    class Gen1<T> : Widget(html) {
        val idDiv: HTMLDivElement by this
        fun add(obj: T) {
            idDiv.innerHTML += obj.toString()
        }
    }


    class HelperItem(val str: String) {
        override fun toString() = "-= $str =-"
    }


    @Test
    fun basic() {
        val target = Widget("""<gen-w id="id1"></gen-w>""")
        val id1 by target { Gen1<HelperItem>() }

        id1.add(HelperItem("foo"))
        id1.add(HelperItem("bar"))
        assert(target.container.innerHTML).contains("-= foo =-", "-= bar =-")

    }

    @Test
    fun bewareOfReentrance() {
        class Target : Widget("""<gen-w id="id1"></gen-w>""") {
            val id1 by this { Gen1<HelperItem>() }
            override fun afterRender() {
                // calling id1 here will trigger reentrance
                // this is fixed using a lazy inside PropertyDelegateProvider of create()
                id1.add(HelperItem("foo2"))
                id1.add(HelperItem("bar2"))
            }
        }

        assert(Target().container.innerHTML).contains("-= foo2 =-", "-= bar2 =-")
    }

    @Test
    fun expandWithoutCallingProperty() {
        class Target : Widget("""<w-gen id="id1"></w-gen>""") {
            // nobody will invoke id1
            val id1 by this { Gen1<HelperItem>() }
        }
        assert(Target().container.innerHTML).contains("w-gen", "id1", html)
    }

    @Test
    fun expand_shouldWorkOnStandardTags() {
        class Target : Widget("""<div id="id1"></div>""") {
            // nobody will invoke id1
            val id1 by this { Gen1<HelperItem>() }
        }

        val target = Target()
        assertTrue { target.container is HTMLDivElement }
        assert(target.container.innerHTML).contains("id1", html)
    }

    @Test
    fun doubleCallBug() {
        var counter = 0

        class Target : Widget("""<w-gen id="id1"></w-gen>""") {
            val id1 by this { counter++; Gen1<HelperItem>() }
        }

        val target = Target()
        target.id1
        assertEquals(1, counter)
    }

}

class TestWidgetFactory {

    @Test
    fun aWidget_withNestedWidget_withWidgetFactory_shouldExpandCorrectly() {
        val widgetFactory = WidgetFactory()
        widgetFactory.register("w-widget1") { Widget("<button>click me</button>") }
        val target = Widget("<w-Widget1></w-Widget1>")
        target.widgetFactory = widgetFactory
        assert(target.container.innerHTML).contains("click me", "w-widget1")
    }

    @Test
    fun aWidget_withNestedWidgetAtLevel2_withWidgetFactory_shouldExpandCorrectly() {
        val widgetFactory = WidgetFactory()
        widgetFactory.register("w-widget1") { Widget("<button>click me</button>") }
        val target = Widget("<span><w-Widget1></w-Widget1></span>")
        target.widgetFactory = widgetFactory
        assert(target.container.innerHTML).contains("click me", "w-widget1")
    }

}

class TestWidgetHolder {
    @Test
    fun shouldEasilyShowAWidget() {
        val target = HolderWidget()
        target.show(twoDivs())
        assert(target.container.innerHTML).contains(twoDivsMarkers)
    }

    @Test
    fun showShouldRemovePreviousWidget() {
        val target = HolderWidget()

        target.show(twoDivs())
        target.show(oneDiv())

        assert(target.container.innerHTML).not.contains(twoDivsMarkers)
    }

    @Test
    fun createByThis_shouldPropagateWidgetHolder() {
        val target = HolderWidget()

        val foo = Widget("""<gen-w id="id1"></gen-w>""")
        val id1 by foo { WidgetWithGenericsTest.Gen1<WidgetWithGenericsTest.HelperItem>() }

        id1.add(WidgetWithGenericsTest.HelperItem("foo"))
        id1.add(WidgetWithGenericsTest.HelperItem("bar"))
        assert(foo.container.innerHTML).contains("-= foo =-", "-= bar =-")
    }

    @Test
    fun closeShouldRestorePreviousWidget() {
        val target = HolderWidget()

        assert(target.container.innerHTML).not.contains(twoDivsMarkers)

        target.show(twoDivs())
        target.show(oneDiv())
        target.show(oneDiv())
        target.closeCurrent()
        target.closeCurrent()

        assert(target.container.innerHTML).contains(twoDivsMarkers)
    }

    @Test
    fun closeShouldNeverRemoveLastWidget() {
        val target = HolderWidget()

        class Child : Widget("""<span>hello</span>""")
        class Parent : Widget("""<div id="child1"></div>""") {
            val child1 by this { Child() }
        }

        val parent = Parent()
        target.show(parent)

        assertSame(target, parent.child1.holderWidget)
    }


//TODO HTMLSpanElement by this when actually it is a HTMLButtonElement
//TODO handle nested widgets
//TODO handle templates
//TODO templates that contains widget, e.g. rackmanager.IpListWidget.html
//TODO handle events, e.g.: afterRender
//TODO HolderWidget is a ResourceWidget. Is it necessary? just for the elementInstance?
//TODO is afterRender() good like this? Do we need to extend a class? or is it enough having a lambda?

}


private fun oneDiv() = Widget("""<div id="div1">foo</div>""")
private fun twoDivs() = Widget("""<div id="div2">bar</div><div id="div3">baz</div>""")

private val twoDivsMarkers = listOf("div2", "bar", "div3", "baz")
private val oneDivMarkers = listOf("div1", "foo")


