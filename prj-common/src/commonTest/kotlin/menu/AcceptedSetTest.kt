package menu

import accesscontrol.AdminAbs
import kotlin.test.Test
import kotlin.test.assertEquals

class AcceptedSetTest {

    @Test
    fun test_emptyRoot_shouldReturnEmpty() {
        val actual = root().acceptedSet(emptySet())
        assertEquals(emptySet(), actual)
    }

    @Test
    fun test_oneChildNoRequirements_shouldReturnIt() {
        val root = root()
        val child0 = root.menu("child0", "")
        val actual = root.acceptedSet(emptySet())
        assertEquals(setOf(child0), actual)
    }

    @Test
    fun test_childNotMetRequirements_shouldNotReturnIt() {
        val root = root()
        val child0 = root.menu("child0", "")
        val child1 = root.menu("child1", "", MockRole(42))
        val actual = root.acceptedSet(emptySet())
        assertEquals(setOf(child0), actual)
    }

    @Test
    fun test_recursion() {
        val root = root()
        val child0 = root.menu("child0", "")
        val child00 = child0.menu("child00", "")
        val actual = root.acceptedSet(emptySet())
        assertEquals(setOf(child0, child00), actual)
    }

    @Test
    fun test_parentNodesWithNoAcceptedChildren_shouldBeRejected() {
        val root = root()
        val child0 = root.menu("child0", "")
        val child00 = child0.menu("child00", "", MockRole(42))
        val child1 = root.menu("child1", "")
        val actual = root.acceptedSet(emptySet())
        assertEquals(setOf(child1), actual)
    }

    @Test
    fun test_admin() {
        val root = root()
        val child0 = root.menu("child0", "")
        val child00 = child0.menu("child00", "", MockRole(42))
        val child1 = root.menu("child1", "")
        val actual = root.acceptedSet(setOf(AdminAbs.id))
        assertEquals(setOf(child0, child00, child1), actual)
    }


    private fun root() = Menu(null, "", "")
}