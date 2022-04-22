package databinding

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

fun <V> bind(target: KMutableProperty0<V>, source: KProperty0<V>) = binder(target, source)

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
    sourceNotifier: ChangesNotifier
) = binder(targetProperty, sourceProperty).also { binder ->
    sourceNotifier.changesListeners.add {
        if (it.propertyName.isNullOrEmpty() || it.propertyName == sourceProperty.name)
            binder.writeTarget()
    }
}

interface Binder {
    fun writeTarget()
    val mode: Mode
}

/** Notifies clients that a property value has changed.
 * A PropertyChanged event is raised when a property is
 * changed on a component. A PropertyChangedEventArgs
 * object specifies the name of the property that changed.
 * PropertyChangedEventArgs provides the PropertyName
 * property to get the name of the property that changed.*/
interface ChangesNotifier {
    /** if notifyChange is called, the listeners will be invoked*/
    val changesListeners: MutableSet<PropertyChangedEvent>
    fun notifyChange(event: PropertyChangedEventArgs)
}


/** Represents the method that will handle the PropertyChanged event
 * raised when a property is changed on a component. */
typealias PropertyChangedEvent = (PropertyChangedEventArgs) -> Unit

/** Provides data for the PropertyChanged event. */
class PropertyChangedEventArgs(
    val sender: Any?,
    /** The name of the property that changed.
     * An Empty value or null for the propertyName parameter indicates that all of the properties have changed. */
    val propertyName: String?
)

class ChangesNotifierDc : ChangesNotifier {
    override val changesListeners = mutableSetOf<PropertyChangedEvent>()
    override fun notifyChange(event: PropertyChangedEventArgs) =
        changesListeners.forEach { it(event) }
}

/**
 * https://docs.microsoft.com/en-us/dotnet/desktop/wpf/data/?view=netdesktop-6.0&redirectedfrom=MSDN#direction-of-the-data-flow
 */
enum class Mode { OneWay }