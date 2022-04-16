package accesscontrol

import kotlin.reflect.KClass

interface RoleAbs {
    val id: Int
    val apiWhitelist: Set<KClass<*>>
    val composedBy: Set<RoleAbs>
}

val AdminAbs = object : RoleAbs {
    override val id: Int = 1
    override val apiWhitelist: Set<KClass<*>> = emptySet()
    override val composedBy: Set<RoleAbs> = emptySet()
}