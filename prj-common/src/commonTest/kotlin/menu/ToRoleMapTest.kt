package menu

import accesscontrol.RoleMeta
import rpc.nameOf
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

class ToRoleMapTest {
    private class RoleDc(
        override val id: Int,
        override val apiAffected: Set<KClass<*>> = emptySet(),
//        override val composedBy: Set<RoleAbs> = emptySet()
    ) : RoleMeta

    private class Api1
    private class Api2
    private class Api3

    @Test
    fun test_one_menu() {
        val role42 = RoleDc(42, setOf(Api1::class, Api2::class))

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
        val role42 = RoleDc(42, setOf(Api1::class, Api2::class))
        val role43 = RoleDc(43, setOf(Api1::class))

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
        val role1 = RoleDc(1, setOf(Api1::class))
        val role2 = RoleDc(2, setOf(Api2::class))
        val role3 = RoleDc(3, setOf(Api3::class)/* composedBy = setOf(role1, role2)*/)
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