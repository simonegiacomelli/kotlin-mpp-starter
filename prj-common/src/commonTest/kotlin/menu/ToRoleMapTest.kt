package menu

import rpc.nameOf
import kotlin.test.Test
import kotlin.test.assertEquals

class ToRoleMapTest {

    private class Api1
    private class Api2
    private class Api3

    @Test
    fun test_one_menu() {
        val role42 = MockRole(42, setOf(Api1::class, Api2::class))

        assertEquals(
            mapOf(
                nameOf(Api1::class) to setOf(42),
                nameOf(Api2::class) to setOf(42),
            ),
            setOf(role42).toRoleMap()
        )
    }

    @Test
    fun test_two_roles() {
        val role42 = MockRole(42, setOf(Api1::class, Api2::class))
        val role43 = MockRole(43, setOf(Api1::class))

        assertEquals(
            mapOf(
                nameOf(Api1::class) to setOf(42, 43),
                nameOf(Api2::class) to setOf(42),
            ),
            setOf(role42, role43).toRoleMap()
        )
    }

    //    @Test
    fun test_composite_roles() {
        val role1 = MockRole(1, setOf(Api1::class))
        val role2 = MockRole(2, setOf(Api2::class))
        val role3 = MockRole(3, setOf(Api3::class)/* composedBy = setOf(role1, role2)*/)
        assertEquals(
            mapOf(
                nameOf(Api1::class) to setOf(1, 3),
                nameOf(Api2::class) to setOf(2, 3),
                nameOf(Api3::class) to setOf(3),
            ),
            setOf(role1, role2, role3).toRoleMap()
        )
    }

}