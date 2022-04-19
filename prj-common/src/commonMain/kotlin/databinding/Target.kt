package databinding

import kotlin.reflect.KMutableProperty0

interface Target<T> {
    val bridge: KMutableProperty0<T>
    fun notifyChange(listener: () -> Unit)
}