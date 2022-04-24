package grid

import kotlin.test.Test
import kotlin.test.assertEquals


class TreeGridWidgetTest {

    data class User(val id: Int) {
        val parent_id: Int? = id.toString().dropLast(1).toIntOrNull()
    }

    @Test
    fun test() {
        val users = listOf(1, 11, 110, 111, 12, 121, 2).map { User(it) }
        val expectedOrder = users
        val expectedDepth = listOf(0, 1, 2, 2, 1, 2, 0)
        val notExpectedOrder = expectedOrder.sortedBy { it.id }

        val target = TreeGridWidget<User>()
        target.elements = notExpectedOrder
        target.properties = mutableListOf(User::id.asProperty())

        val childrenFor = users.buildChildrenMap { it.parent_id }

        target.onChildrenFor = { user -> childrenFor[user?.id] ?: emptyList() }
        target.render()

        assertEquals(expectedOrder, target.elements)
        assertEquals(expectedDepth, target.elements.indices.map { target.depthFor(it) })
        assertEquals(expectedDepth, target.elements.map { target.depthFor(it) })
    }
}