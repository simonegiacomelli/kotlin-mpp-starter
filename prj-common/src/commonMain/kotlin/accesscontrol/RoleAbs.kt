package accesscontrol

import kotlin.reflect.KClass

interface RoleAbs {
    val id: Int
}

interface RoleMeta : RoleAbs {
    val apiAffected: Set<KClass<*>>
//    val composedBy: Set<RoleAbs>
}

val AdminAbs = object : RoleAbs {
    override val id: Int = 1
}