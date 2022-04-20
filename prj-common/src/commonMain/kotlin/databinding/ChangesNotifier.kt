package databinding

import kotlin.reflect.KProperty

interface ChangesNotifier {
    /** if changes happen, the listener will be invoked*/
    fun addChangeListener(listener: (property: KProperty<*>) -> Unit)
}