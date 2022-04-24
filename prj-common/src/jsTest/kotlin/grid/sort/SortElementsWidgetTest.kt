package grid.sort

import extensions.cells
import grid.asProperty
import org.w3c.dom.HTMLTableRowElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

private data class User(val name: String, val age: Int)

class OrderElementsWidgetTest {

    private val alice = User("Alice", 42)
    private val angel = User("Angel", 44)
    private val bella = User("Bella", 43)

    private val userElements = listOf(alice, angel, bella)


    @Test
    fun test_changeOrder() {
        // GIVEN
        val target = newTarget()

        // WHEN
        target.htmlRowFor(bella).click()
        target.htmlMoveUp().click()

        // THEN
        val expected1 = listOf(alice, bella, angel)
        verifyElementsOrder(expected1, target)

        // WHEN 2
        target.htmlRowFor(alice).click() // should do nothing

        // THEN 2
        verifyElementsOrder(expected1, target)

        // WHEN 3
        target.htmlMoveDown().click()
        target.htmlMoveDown().click()

        // THEN 3
        verifyElementsOrder(listOf(bella, angel, alice), target)
    }


    @Test
    fun test_properties() {
        val target = newTarget()
        verifyElementsOrder(userElements, target)
    }

    @Test
    fun test_selectedElements() {
        val target = newTarget()
        target.htmlCheckboxCellFor(alice).click()
        target.htmlCheckboxFor(angel).click()

        assertEquals(listOf(alice, angel), target.selectedElements)
    }

    @Test
    fun test_issue_addAnotherColumnWhenElementClick() {
        // GIVEN
        val target = newTarget()
        fun headStrings(target: SortElementsWidget<User>) = target.htmlHeadRow().cells().map { it.innerHTML }
        val header = headStrings(target)

        // WHEN
        target.htmlRowFor(bella).click()

        // THEN
        assertEquals(header, headStrings(target))
    }


    private fun verifyElementsOrder(
        expectedOrder: List<User>, target: SortElementsWidget<User>
    ) {
        assertEquals(expectedOrder, target.elements)
        fun stringsOf(u: User) = setOf(u.name, "${u.age}")
        expectedOrder.forEachIndexed { index, user ->
            val row = target.htmlRowFor(index)
            val htmlSet = row.cells().map { it.innerHTML }.toSet()
            val needAll = stringsOf(user)
            val missing = needAll.minus(htmlSet)
            if (missing.isNotEmpty()) fail("User `$user`, missing strings: `$missing`. The html has: `$htmlSet`")
        }
    }

    private fun newTarget(block: (SortElementsWidget<User>) -> Unit = {}): SortElementsWidget<User> {
        val target = SortElementsWidget<User>()
        block(target)
        target.properties.add(User::name.asProperty())
        target.properties.add(User::age.asProperty())

        target.elements = userElements.toMutableList()
        target.render()
        return target
    }

}

private fun HTMLTableRowElement.println() {
    val row = outerHTML
    println("`$row`\n")
}
