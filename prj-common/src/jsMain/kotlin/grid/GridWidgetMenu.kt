package grid

import widget.Widget

fun gridWidgetDemo(): Widget = run {
    class User(val name: String, val age: Int)

    val containerWidget = Widget(//language=HTML
        """<h2>GridWidget</h2>
        <div id='gridWidget'></div>
    """
    )
    val gridWidget by containerWidget { GridWidget<User>() }


    val elements = listOf(User("Foo", 42), User("Bar", 43))

    gridWidget.properties = mutableListOf(User::name.asProperty(), User::age.asProperty())
    gridWidget.elements = elements.toMutableList()
    gridWidget.render()

    containerWidget
}

