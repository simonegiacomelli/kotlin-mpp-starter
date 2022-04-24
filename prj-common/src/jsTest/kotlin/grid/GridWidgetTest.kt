package grid

import extensions.*
import grid.GridWidgetTest.OnCellRenderHelper.render
import kotlinx.coroutines.test.runTest
import org.w3c.dom.HTMLTableElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class GridWidgetTest : Shared() {


    @Test
    fun test_simpleRender() {
        val target = newTarget()
        target.render()
        assertSimpleRender(target)
    }


    @Test
    fun test_htmlRowFor() {
        val target = newTarget()
        target.render()
        val expected = listOf("Baz", "44")
        assertEquals(expected, target.htmlRowFor(userBaz).cells().map { it.innerHTML })
        assertEquals(expected, target.htmlRowFor(1).cells().map { it.innerHTML })
    }

    @Test
    fun test_elementInfoFor() {
        val target = newTarget()
        target.render()
//        val rows = target.table.tbodyFirst().rows()
        userElements.forEachIndexed { index, user ->
            val info = target.elementInfoFor(index)
            assertSame(info, target.elementInfoFor(user))
            assertSame(user, info.element)

        }

    }

    @Test
    fun test_ordering() {
        val target = newTarget()
        target.ordering = Ordering("age", false)
        target.render()

        target.table.apply {
            assertHeadIs(listOf("name", "age↓"))
            assertBodyIs(elementsDescending)
        }
    }

    @Test
    fun test_ordering_emptyCustomOrder_shouldAffectNothing() {
        val target = newTarget()
        target.ordering = Ordering("age", false)
        target.onCustomOrder = { }
        target.render()

        target.table.apply {
            assertHeadIs(listOf("name", "age↓"))
            assertBodyIs(elementsDescending)
        }
    }

    @Test
    fun test_custom_ordering() {
        //        User("Foo", 42),
        //        User("Baz", 44),
        //        User("Bar", 43),

        val target = newTarget()
        target.ordering = Ordering("age", true)
        target.onCustomOrder = { comparators.add(0, compareBy { it.name[0] }) }
        target.render()


        target.table.apply {
            assertHeadIs(listOf("name", "age↑"))
            assertBodyIs(
                listOf(
                    listOf("Bar", "43"),
                    listOf("Baz", "44"),
                    listOf("Foo", "42"),
                )
            )
        }
    }

    @Test
    fun test_orderingAndHeadClick_withClick() {
        val target = newTarget()
        target.render()

        val events = mutableListOf<PropertyEvent<User>>()
        target.onHeadClick = { events.add(this) }

        fun assertHeadClickOnAge() {
            assertEquals(1, events.size)
            assertEquals("age", events.first().property.name)
            events.clear()
        }


        target.table.apply {

            theadFirst()[1, 1].click()
            assertHeadIs(listOf("name", "age↑"))
            assertBodyIs(elementsAscending)
            assertHeadClickOnAge()


            theadFirst()[1, 1].click()
            assertHeadIs(listOf("name", "age↓"))
            assertBodyIs(elementsDescending)
            assertHeadClickOnAge()

            theadFirst()[1, 1].click()
            assertHeadIs(listOf("name", "age"))
            assertBodyIs(elementsOriginalOrder)
            assertHeadClickOnAge()

        }

        target.sortableHead = false
        target.table.apply {
            theadFirst()[1, 1].click()
            assertHeadIs(listOf("name", "age"))
            assertBodyIs(elementsOriginalOrder)
            assertHeadClickOnAge()
        }
    }

    @Test
    fun test_grid_onCellRender() {
        val target = newTarget()
        target.onDataRender = { if (property.name == "age") render(this) }

        target.render()
        OnCellRenderHelper.verify(target)
    }

    @Test
    fun test_property_onCellRender() {
        val target = newTarget()

        target.properties
            .first { it.name == "age" }
            .onDataRender = { render(this) }

        target.render()
        OnCellRenderHelper.verify(target)
    }

    internal object OnCellRenderHelper {

        fun render(event: ValueEvent<User>) = event.apply {
            cell.innerHTML = "[${element.age}]"
        }

        fun verify(target: GridWidget<User>) {
            target.table.assertBodyIs(
                listOf(
                    listOf("Foo", "[42]"),
                    listOf("Baz", "[44]"),
                    listOf("Bar", "[43]")
                )
            )
        }
    }

    @Test
    fun test_head_th_and_body_td() {
        val target = newTarget().render()
        target.table.theadFirst()[0].cells().forEach {
            assertEquals("TH", it.tagName)
        }
        target.table.tbodyFirst().rows().flatMap { it.cells() }.forEach {
            assertEquals("TD", it.tagName)
        }
    }

    @Test
    fun test_property_captions() {
        val target = newTarget().render()
        target.properties.forEach { it.caption = it.caption.uppercase() }
        target.render()
        target.table.assertHeadIs(listOf("NAME", "AGE"))
    }

    @Test
    fun test_grid_onHeadRender() {
        val target = newTarget()
        target.onHeadRender = { cell.innerHTML += "-modified" }
        target.render()
        target.table.assertHeadIs(listOf("name-modified", "age-modified"))
    }

    @Test
    fun test_property_onHeadRender() {
        val target = newTarget()
        val name = target.properties[0]
        val age = target.properties[1]
        name.onHeadRender = { cell.innerHTML += "-a" }
        age.onHeadRender = { cell.innerHTML += "-b" }
        target.render()
        target.table.assertHeadIs(listOf("name-a", "age-b"))
    }

    @Test
    fun test_grid_onDataClick() {
        val events = mutableListOf<ValueEvent<User>>()
        val target = newTarget()
        // la seguente riga e' per verificare che l'onclick possa essere utilizzato comunque e non vada in conflitto
        target.onDataRender = { this.cell.onclick = { println("onclick"); events.add(this) } }
        target.render()
        target.onDataClick = { println("dataclick"); events.add(this); }
        target.table.tbodyFirst()[0, 0].click() // Foo
        target.table.tbodyFirst()[0, 1].click() // 42
        target.table.tbodyFirst()[2, 0].click() // Bar

        assertEquals(listOf("Foo", "Foo", 42, 42, "Bar", "Bar"), events.map { it.value })
        assertEquals(listOf(0, 0, 1, 1, 0, 0), events.map { it.propertyIndex })
    }

    @Test
    fun test_add_calculatedProperty() {
        val target = newTarget()
        target.properties.add(0, User::name.asProperty(caption = "combine") { "$name-$age" })

        target.render()
        target.table.apply {
            assertHeadIs(listOf("combine", "name", "age"))
            assertBodyIs(
                listOf(
                    listOf("Foo-42", "Foo", "42"),
                    listOf("Baz-44", "Baz", "44"),
                    listOf("Bar-43", "Bar", "43")
                )
            )
        }
    }

    @Test
    fun test_grid_onElementClick() {
        val events = mutableListOf<ElementEvent<User>>()
        val target = newTarget()
        // la prossima riga e' per verificare che l'onclick sul tr non vada in conflitto con OnElementClick
        target.onDataRender = { if (propertyIndex == 0) tr.onclick = { events.add(this) } }
        target.render()

        target.onElementClick = { events.add(this) }
        target.table.tbodyFirst()[0, 0].click() //click cell
        target.table.tbodyFirst()[1, 1].click() //click cell
        target.table.tbodyFirst()[2].click() //click row

        val e0 = userElements[0]
        val e1 = userElements[1]
        val e2 = userElements[2]
        val actual = events.map { listOf(it.element, it.elementIndex) }.flatten()
        val expected = listOf(e0, 0, e0, 0) + listOf(e1, 1, e1, 1) + listOf(e2, 2, e2, 2)


        assertEquals(expected, actual)
    }

    @Test
    fun test_renderPropertySettings() = runTest {
        val target = newTarget()

        // we are going to hide this column
        // this ordering should be ignored
        target.ordering = Ordering("name", true)
        target.render(Settings(hidden = setOf("name")))
        target.table.apply {
            assertHeadIs(listOf("age"))
            assertBodyIs(elementsOriginalOrder.map { it.drop(1) })
        }

        target.ordering = null
        target.render(Settings())
        assertSimpleRender(target)

        target.render(Settings(order = listOf("age", "name"), hidden = setOf("FAKE")))
        target.table.apply {
            assertHeadIs(listOf("age", "name"))
            assertBodyIs(
                listOf(
                    listOf("42", "Foo"),
                    listOf("44", "Baz"),
                    listOf("43", "Bar")
                )
            )
        }


    }

    @Test
    fun test_focusedElement() {
        val target = newTarget()
        target.render()
        val bgColor = "#123456"

        fun verifyFocusedElement(user: User?) {
            assertEquals(user, target.focusedElement, "assertEquals focusedElement `$user`")
            if (user != null) assertNotEquals(
                bgColor,
                target.htmlRowFor(user).style.backgroundColor,
                "assertNotEquals bgColor `$user`"
            )
            val nonFocusedUsers = userElements.toSet().run { if (user == null) this else minus(user) }
            nonFocusedUsers.forEach { nonFocusedUser ->
                assertEquals(
                    "", target.htmlRowFor(nonFocusedUser).style.backgroundColor,
                    "assertEquals bgColor `$user`"
                )
            }
        }

        target.focusedBackgroundColor = bgColor

        // click should not change focusedElement
        userElements.forEach { user ->
            target.htmlRowFor(user).click()
            verifyFocusedElement(null)
        }

        target.focusedElementChangeOnClick = true

        userElements.forEach { user ->
            target.htmlRowFor(user).click()
            verifyFocusedElement(user)
        }

        userElements.forEach { user ->
            target.htmlRowFor(user).click()
            target.render()
            verifyFocusedElement(user)
        }

    }

    internal fun assertSimpleRender(target: GridWidget<User>) {
        target.table.apply {
            assertHeadIs(listOf("name", "age"))
            assertBodyIs(elementsOriginalOrder)
        }
    }


    internal fun newTarget() =
        GridWidget<User>().also { it.properties = userProperties; it.elements = userElements }


}


internal fun HTMLTableElement.assertBodyIs(expected: List<List<String>>, message: String? = null) {
    val actual = this.tbodyFirst().rows().map { it.cells().map { it.innerHTML } }
    val m = if (message == null) null else "assertBodyIs $message"
    assertEquals(expected, actual, m)
}
