package menu

import accesscontrol.allows

fun Menu.filterFor(roles: Set<Int>): Menu {
    Menu(null, name, caption)
    return this
}

private fun Menu.filterDFS(accept: (Menu) -> Boolean): Menu {
    TODO()
}

// https://en.wikipedia.org/wiki/Tree_(data_structure)#Terminology
fun Menu.acceptedSet(userRoles: Set<Int>): Set<Menu> = buildSet {
    fun List<Menu>.recurse(): Int =
        filter { menu ->
            val requiredRoles = menu.roles.map { it.id }.toSet()
            val allowed = requiredRoles.allows(userRoles)
            val accepted = allowed && (menu.children.isEmpty() || menu.children.recurse() > 0)
            if (accepted) add(menu)
            accepted
        }.size


    children.recurse()
}