package grid

import extensions.extVisible
import extensions.get
import extensions.rows
import extensions.tbodyFirst
import org.w3c.dom.HTMLTableElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame


class LazyGridWidgetTest : Shared() {
    //todo verify target == gridEvent.grid (because of this leaking in inheritance)

    @Test
    fun test_lazyLoading() {
        // GIVEN
        val generatedElements = (1..5).map { User("name$it", it) }

        val expectPage1 = listOf(
            listOf("name1", "1"),
            listOf("name2", "2")
        )
        val expectPage2 = expectPage1 + listOf(
            listOf("name3", "3"),
            listOf("name4", "4"),
        )
        val expectPage3 = expectPage2 + listOf(
            listOf("name5", "5"),
        )

        // WHEN
        val itemsPerPage = 2
        val target = LazyGridWidget<User>().also { it.properties = userProperties; it.elements = userElements }
        target.assertLoadMoreElementVisibility(false, "At startup") // then
        target.pagination.itemsPerPage = itemsPerPage
        target.onLazyLoad = {
            elements = generatedElements.chunked(pagination.itemsPerPage)[pagination.index]
            loadDone()
        }

        (1..3).forEach {
            val round = "- round $it"
            target.render()

            // THEN

            // page 1
            target.assertLoadMoreElementVisibility(true, "Page 1 $round")
            target.table.assertContent(propertiesHeader, expectPage1, "expect page 1 $round")
            target.verifyGridElementsAndElementClick(generatedElements.take(2), "Page 1 click")
            target.verifyRowFor(generatedElements.take(2))

            // page 2
            target.loadMoreElement.click()
            target.assertLoadMoreElementVisibility(true, "Page 2 $round")
            target.table.assertContent(propertiesHeader, expectPage2, "expect page 2 $round")
            target.verifyGridElementsAndElementClick(generatedElements.take(4), "Page 2 click")

            // page 3
            target.loadMoreElement.click()
            target.assertLoadMoreElementVisibility(false, "Page 3")
            target.table.assertContent(propertiesHeader, expectPage3, "expect page 3 $round")
            target.verifyGridElementsAndElementClick(generatedElements, "Page 3 click")
        }


    }

    private fun LazyGridWidget<User>.verifyRowFor(actualElements: List<User>) {
        val rows = table.tbodyFirst().rows()
        assertEquals(actualElements.size, rows.size)
        // check every index/element
        actualElements.forEachIndexed { index, element ->
            val rowForIndex = htmlRowFor(index)
            val rowForElement = htmlRowFor(element)
            assertSame(rowForElement, rowForIndex)
            val actualRowIndex = rows.indexOf(rowForElement)
            assertEquals(index, actualRowIndex)
        }
    }

    fun LazyGridWidget<*>.assertLoadMoreElementVisibility(visible: Boolean, context: String) {
        val msg = context + ". LoadMoreElement should be " + if (visible) "visible" else "not visible"
        assertEquals(visible, loadMoreElement.extVisible, msg)
    }

    internal fun GridWidget<User>.verifyGridElementsAndElementClick(
        expectedElements: List<User>,
        message: String
    ) {

        assertSameElements(expectedElements, elements, "$message grid elements same failed")

        verifyElementClick(expectedElements, "$message row click") { row -> row.click() }
        verifyElementClick(expectedElements, "$message row.cells[0] click") { row -> row[0].click() }
        verifyElementClick(expectedElements, "$message row.cells[1] click") { row -> row[1].click() }
    }

    internal fun HTMLTableElement.assertContent(head: List<String>, body: List<List<String>>, message: String? = null) {
        apply {
            assertHeadIs(head, message)
            assertBodyIs(body, message)
        }
    }

}

