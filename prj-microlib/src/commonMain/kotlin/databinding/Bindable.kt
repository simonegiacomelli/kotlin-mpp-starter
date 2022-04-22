package databinding

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

fun <V> bind(source: KProperty0<V>, target: KMutableProperty0<V>) = binder(target, source)

private fun <V> binder(
    targetProperty: KMutableProperty0<V>,
    sourceProperty: KProperty0<V>
): Binder {
    return object : Binder {
        override fun writeTarget() = targetProperty.set(sourceProperty.get())
        override val mode: Mode = Mode.OneWay
    }.apply { writeTarget() }
}

fun <V> bind(
    targetProperty: KMutableProperty0<V>,
    sourceProperty: KProperty0<V>,
    sourceNotifier: NotifyPropertyChanged
) = binder(targetProperty, sourceProperty).also { binder ->
    sourceNotifier.propertyChangedEventHandlers.add {
        if (it.propertyName == null || it.propertyName == sourceProperty.name)
            binder.writeTarget()
    }
}

interface Binder {
    fun writeTarget()
    val mode: Mode
}

interface NotifyPropertyChanged {
    /** if changes happen, the listener will be invoked*/
    val propertyChangedEventHandlers: MutableSet<PropertyChangedEvent>
    fun notifyPropertyChangedEvent(event: PropertyChangedEventArgs)
}


typealias PropertyChangedEvent = (PropertyChangedEventArgs) -> Unit

class PropertyChangedEventArgs(val sender: Any?, val propertyName: String?)

class ChangesNotifierDc : NotifyPropertyChanged {
    override val propertyChangedEventHandlers = mutableSetOf<PropertyChangedEvent>()
    override fun notifyPropertyChangedEvent(event: PropertyChangedEventArgs) =
        propertyChangedEventHandlers.forEach { it(event) }
}

enum class Mode { OneWay }