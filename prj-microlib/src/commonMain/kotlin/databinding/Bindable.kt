package databinding

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

fun <V> bind(kProperty0: KProperty0<V>): BindableMnNnRn<V> = object : BindableMnNnRn<V> {
    override val property: KProperty0<V>
        get() = kProperty0
}

fun <V> bind(
    source: KProperty0<V>,
    target: KMutableProperty0<V>
): Binder = object : Binder {
    override fun writeTarget() = target.set(source.get())
}.apply { writeTarget() }

interface Binder {
    fun writeTarget()
}

interface BindableMnNnRn<V> {
    val property: KProperty0<V>
}

interface BindableMyNnRn<V> : BindableMnNnRn<V> {
    override val property: KMutableProperty0<V>
}

interface Binding
