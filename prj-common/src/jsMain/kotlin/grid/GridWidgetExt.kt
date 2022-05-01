package grid

import sort.sortAccording

private class SettingsObserver<E>(private val settings: Settings) : GridObserver<E, MapPropertiesEvent<E>> {

    override fun notify(event: MapPropertiesEvent<E>) {
        val props = event.properties.filterNot { settings.hidden.contains(it.name) }.toMutableList()
        val newProps = props.sortAccording(settings.order) { it.name }
        event.properties.clear()
        event.properties.addAll(newProps)
    }
}

fun <E> GridWidget<E>.render(settings: Settings) {
    observersFor<MapPropertiesEvent<E>>().removeAll { it is SettingsObserver }
    addObserver(SettingsObserver(settings))
    render()
}

