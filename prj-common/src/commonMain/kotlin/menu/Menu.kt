package menu

import accesscontrol.RoleAbs

open class Menu(
    val parent: Menu?,
    val name: String,
    val caption: String,
    vararg roleArray: RoleAbs,
) {
    val roles: Set<RoleAbs> = roleArray.toSet()
    val children: MutableList<Menu> = mutableListOf()

    init {
        parent?.also { parent -> parent.children.add(this) }
    }

    override fun toString(): String = "Menu(name=$name,${roles.map { it.id }})"
}

fun Menu.menu(
    name: String,
    caption: String,
    vararg roles: RoleAbs
): Menu = Menu(this, name, caption, *roles)

