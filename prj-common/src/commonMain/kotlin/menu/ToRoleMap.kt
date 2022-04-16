package menu

import accesscontrol.RoleMeta
import rpc.nameOf


fun Set<RoleMeta>.toRoleMap(): Map<String, Set<Int>> {

    val map = mutableMapOf<String, MutableSet<Int>>()
    fun accumulate(name: String, roleId: Int) = map.getOrPut(name) { mutableSetOf() }.add(roleId)

    forEach { role ->
        role.apiAffected.map { nameOf(it) }.forEach { name ->
            accumulate(name, role.id)
        }
    }
    return map
}