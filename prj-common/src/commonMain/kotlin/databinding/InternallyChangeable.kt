package databinding

import kotlin.reflect.KProperty

interface InternallyChangeable {
    /** if changes happen, the listener will be invoked*/
    fun onChange(listener: (property: KProperty<*>) -> Unit)
}