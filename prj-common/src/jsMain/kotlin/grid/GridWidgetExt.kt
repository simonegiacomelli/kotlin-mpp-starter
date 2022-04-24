package grid

import sort.sortAccording

fun <E> GridWidget<E>.render(settings: Settings) {
    val op = onProperties
    if (op != null) error("l'evento onProperties non puo' essere usato contemporaneamente a questa funzione")
    onProperties = {
        val props = properties.filterNot { settings.hidden.contains(it.name) }.toMutableList()
        props.sortAccording(settings.order) { it.name }
    }
    onInternalRender = { render(settings) }
    render()
    onProperties = null
}

