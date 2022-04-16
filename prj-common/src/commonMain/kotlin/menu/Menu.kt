package menu

import accesscontrol.AdminAbs
import accesscontrol.RoleAbs

open class Menu(
    val parent: Menu?,
    val name: String,
    val caption: String,
    val roles: Set<RoleAbs> = emptySet(),
) {
    val children: MutableList<Menu> = mutableListOf()

    init {
        parent?.also { parent -> parent.children.add(this) }
    }
}


fun Menu.menu(
    name: String,
    caption: String,
    vararg roles: RoleAbs = emptyArray()
): Menu = Menu(this, name, caption, roles.toSet() + setOf(AdminAbs))

