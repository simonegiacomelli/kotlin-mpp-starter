package databinding

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

interface Target<T> : InternallyChangeable {
    val property: KMutableProperty0<T>
}

interface InternallyChangeable {
    /** if changes happen, the listener will be invoked*/
    fun onChange(listener: (property: KProperty<*>) -> Unit)
}

interface ExternallyChangeable {
    /** this will change the property value and will notify
     * registered listeners, except the originator if available */
    fun change(property: KProperty<*>, value: Any?, originator: ((property: KProperty<*>) -> Unit)?)
    fun change(property: KProperty<*>, value: Any?) = change(property, value, null)
}
