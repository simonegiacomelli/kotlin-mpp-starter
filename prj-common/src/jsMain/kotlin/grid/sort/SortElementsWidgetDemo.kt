package grid.sort

import grid.asProperty
import widget.Widget
import widget.afterRender

fun sortElementsWidgetDemo(): Widget {
    data class User(val name: String, val age: Int)

    val users = listOf(
        User("foo", 42),
        User("Bar", 43),
        User("Baz", 44),
        User("Etc", 45),
    )

    return SortElementsWidget<User>().apply {
        afterRender {
            properties = mutableListOf(
                User::name.asProperty("Name"),
                User::age.asProperty("Age")
            )
            elements = users.toMutableList()
            focusedElement = elements.firstOrNull()
            render()
        }
    }

}