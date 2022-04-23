package databinding.v2


import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

fun <V> bind(target: KMutableProperty0<V>, source: KProperty0<V>) = bind(target.toProperty0(), source.toProperty0())

fun <V> bind(targetProperty: MutableProperty0<V>, sourceProperty: Property0<V>): BinderStoT {
    return object : BinderStoT {
        override fun writeTarget() = targetProperty.set(sourceProperty.get())
    }.apply { writeTarget() }
}

fun <V> bind(targetProperty: MutableProperty0<V>, sourceProperty: Property0CN<V>): BinderStoTCN {
    val binderStoT = bind(targetProperty, sourceProperty as Property0<V>)
    sourceProperty.changesListeners.add {
        if (it.propertyName.isNullOrEmpty() || it.propertyName == sourceProperty.name) binderStoT.writeTarget()
    }
    return object : BinderStoTCN, BinderStoT by binderStoT, ChangesNotifier by sourceProperty {}
}

interface BinderStoT {
    fun writeTarget()
}

interface BinderStoTCN : BinderStoT, ChangesNotifier


fun <V> KProperty0<V>.toProperty0(): Property0<V> {
    val prop = this
    return object : Property0<V> {
        override val name: String get() = prop.name
        override fun get(): V = prop.get()
    }
}

fun <V> KProperty0<V>.toProperty0Notify(changesNotifier: ChangesNotifier = ChangesNotifierDc()): Property0CN<V> =
    object : Property0CN<V>, Property0<V> by toProperty0(), ChangesNotifier by changesNotifier {}

fun <V> KMutableProperty0<V>.toProperty0(): MutableProperty0<V> {
    val prop = this
    return object : MutableProperty0<V> {
        override fun set(value: V) = prop.set(value)
        override fun get(): V = prop.get()
        override val name: String get() = prop.name
    }
}

interface Named {
    val name: String
    val caption: String get() = name
}

interface Property1<E, out V> : Named {
    fun get(receiver: E): V
}

interface MutableProperty1<E, V> : Property1<E, V> {
    fun set(receiver: E, value: V)
}

interface Property0<out V> : Named {
    fun get(): V
}

interface Property0CN<out V> : Property0<V>, ChangesNotifier

interface MutableProperty0<V> : Property0<V> {
    fun set(value: V)
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

class ChangesNotifierDc : ChangesNotifier {
    override val changesListeners = mutableSetOf<PropertyChangedEvent>()
    override fun notifyChange(event: PropertyChangedEventArgs) =
        changesListeners.forEach { it(event) }
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


/**
 * https://docs.microsoft.com/en-us/dotnet/desktop/wpf/data/?view=netdesktop-6.0&redirectedfrom=MSDN#direction-of-the-data-flow
 */
enum class Mode { OneWay, TwoWay }