package properties

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

inline fun <reified T> forward(crossinline function: () -> KMutableProperty0<T>):
        PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>> =
    PropertyDelegateProvider { _: Any?, property ->
        val x = object : ReadWriteProperty<Any?, T> {

            override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                return function().get()
            }

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                function().set(value)
            }

        }
        x
    }