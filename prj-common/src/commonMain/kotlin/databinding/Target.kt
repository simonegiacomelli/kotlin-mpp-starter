package databinding

import kotlin.reflect.KMutableProperty0

interface Target<T> {
    val bridge: KMutableProperty0<T>

    /** if changes happen, the listener will be invoked*/
    fun onChange(listener: () -> Unit)
}

interface Target2<T>