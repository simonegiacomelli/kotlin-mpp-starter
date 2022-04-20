package databinding

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

interface Target<T> : InternallyChangeable {
    val property: KMutableProperty0<T>
}

interface InternallyChangeable {
    /** if changes happen, the listener will be invoked*/
    fun onChange(listener: (property: KProperty<*>) -> Unit): (KProperty<*>) -> Unit
}

interface ExternallyChangeable {

    fun change(
        property: KProperty<*>,
        value: Any?,
        originator: ((property: KProperty<*>) -> Unit)? = null
    )
}

interface Target2<T>