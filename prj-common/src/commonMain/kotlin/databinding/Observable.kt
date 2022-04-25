package databinding

import kotlin.reflect.KProperty

interface Observable {
    /** if changes happen, the listener will be invoked*/
    fun addObserver(observer: (property: KProperty<*>) -> Unit)
}