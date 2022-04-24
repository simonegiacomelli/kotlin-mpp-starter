package grid

import extensions.cells
import extensions.rows
import extensions.tbodyFirst
import extensions.theadFirst
import org.w3c.dom.HTMLTableElement
import org.w3c.dom.HTMLTableRowElement
import kotlin.test.assertEquals
import kotlin.test.assertSame


data class User(val name: String, val age: Int)

open class Shared {
    val userFoo = User("Foo", 42)
    val userBaz = User("Baz", 44)
    val userBar = User("Bar", 43)

    val userElements = listOf(userFoo, userBaz, userBar)

    val userProperties: MutableList<Property<User, *>> = listOf(
        User::name.asProperty(),
        User::age.asProperty()
    ).toMutableList()

    val elementsAscending = listOf(
        listOf("Foo", "42"),
        listOf("Bar", "43"),
        listOf("Baz", "44"),
    )

    val elementsDescending = listOf(
        listOf("Baz", "44"),
        listOf("Bar", "43"),
        listOf("Foo", "42")
    )

    val elementsOriginalOrder = listOf(
        listOf("Foo", "42"),
        listOf("Baz", "44"),
        listOf("Bar", "43")
    )

    val propertiesHeader = listOf("name", "age")

    fun GridWidget<User>.verifyElementClick(
        expectedElements: List<User>,
        message: String,
        action: (HTMLTableRowElement) -> Unit
    ) {
        val events = mutableListOf<ElementEvent<User>>()
        onElementClick = { events.add(this) }

        table.tbodyFirst().rows().forEach(action)

        val actualElements = events.map { it.element }

        assertEquals(expectedElements, actualElements, "$message elementClick equals failed")
        assertSameElements(expectedElements, actualElements, "$message elementClick same failed")

        val actualIndexes = events.map { it.elementIndex }
        assertEquals(expectedElements.indices.toList(), actualIndexes, "$message indexes failed")
    }

    fun assertSameElements(
        expectedElements: List<User>,
        actualElements: List<User>,
        message1: String
    ) {
        expectedElements.zip(actualElements).map {
            assertSame(it.first, it.second, message1)
        }
    }

    internal fun HTMLTableElement.assertHeadIs(expected: List<String>, message: String? = null) {
        val actual = this.theadFirst().rows().map { it.cells().map { it.innerHTML } }.getOrNull(1)
        val m = if (message == null) null else "assertHeadIs $message"
        assertEquals(expected, actual, m)
    }

}