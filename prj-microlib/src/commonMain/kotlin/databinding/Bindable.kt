package databinding

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

fun <V> bind(
    source: KProperty0<V>,
    target: KMutableProperty0<V>
): Binder = object : Binder {
    override fun writeTarget() = target.set(source.get())
}.apply { writeTarget() }

fun <V> bind(
    sourceNotifier: ChangesNotifier,
    source: KProperty0<V>,
    target: KMutableProperty0<V>
): Binder {
    val binder = object : Binder {
        override fun writeTarget() = target.set(source.get())
    }
    sourceNotifier.addChangeListener { binder.writeTarget() }
    binder.writeTarget()
    return binder
}

interface Binder {
    fun writeTarget()
}

interface ChangesNotifier {
    /** if changes happen, the listener will be invoked*/
    fun addChangeListener(listener: () -> Unit)
    fun notifyListeners()
}

class ChangesNotifierDc : ChangesNotifier {
    private val changeListeners = mutableListOf<() -> Unit>()
    override fun addChangeListener(listener: () -> Unit): Unit = run { changeListeners.add(listener) }
    override fun notifyListeners() = changeListeners.forEach { it() }
}