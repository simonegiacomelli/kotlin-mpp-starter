package menu

import accesscontrol.RoleMeta
import kotlin.reflect.KClass

class MockRole(
    override val id: Int,
    override val apiAffected: Set<KClass<*>> = emptySet(),
) : RoleMeta