package pages.bootstrap.databinding.demo

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class User2 {
    inline operator fun <reified T, reified E> provideDelegate(
        thisRef: Any?,
        prop: KProperty<*>
    ): ReadWriteProperty<E, T> {
        //if(T::class == Boolean::class)
        return ResourceDelegate<E, T>()
    }


}

class ResourceDelegate<E, T> : ReadWriteProperty<E, T> {
    override fun getValue(thisRef: E, property: KProperty<*>): T {
        TODO()
    }

    override fun setValue(thisRef: E, property: KProperty<*>, value: T) {
        TODO("Not yet implemented")
    }
}