package grid

import widget.Widget

fun treeGridWidgetDemo(): Widget {

    val hierarchy = listOf(1, 11, 110, 111, 12, 121, 2)
    val names = listOf("Benz", "Gauss", "Golick", "Carson", "Kalam", "Cray", "Keldysh")
    val users = hierarchy.zip(names).map { User(it.first, it.second) }

    val target = TreeGridWidget<User>()
    target.elements = users
    target.properties = mutableListOf(
        User::name.asProperty("Code", onDataRender = { onDataRender() }),
        User::name.asProperty("Name"),
        User::id.asProperty()
    )

    val childrenFor = users.groupBy { it.parent_id }
    target.onChildrenFor = { user -> childrenFor[user?.id] ?: emptyList() }
    target.focusedElementChangeOnClick = true
//    target.focusedBackgroundColor = "green"
    target.render()

    return target
}

private fun ValueEvent<User>.onDataRender() {
    val grid = grid as TreeGridWidget<User>
    val indent = "&nbsp;".repeat(grid.depthFor(elementIndex) * 5)
    cell.innerHTML = indent + element.run { name }
}

private data class User(val id: Int, val name: String) {
    val parent_id: Int? = id.toString().dropLast(1).toIntOrNull()
}