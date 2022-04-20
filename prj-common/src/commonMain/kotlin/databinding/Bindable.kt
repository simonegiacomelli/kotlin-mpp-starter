package databinding

import serialization.AnyValue
import serialization.toAnyValue
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

//class ChangeListener(val notify: (property: KProperty<*>) -> Unit)


/**
 * cannot be @Serializable! on 2022-04-20 breaks the ir-js compiler!
 * workaround: a custom serializer for every descendant of Bindable
 */
open class Bindable : InternallyChangeable, ExternallyChangeable {

    /** this is the problematic property that breaks the compiler */
    val bindingValueMap: LinkedHashMap<String, AnyValue> = LinkedHashMap()

    val bindingListeners = mutableListOf<(property: KProperty<*>) -> Unit>()

    override fun onChange(changeListener: (property: KProperty<*>) -> Unit) {
        bindingListeners.add(changeListener)
    }

    override fun change(property: KProperty<*>, value: Any?, originator: ((property: KProperty<*>) -> Unit)?) {
        bindingSetValue(property, value)
        notifyChange(property, originator)
    }

    fun notifyChange(property: KProperty<*>, originator: ((property: KProperty<*>) -> Unit)? = null) {
        bindingListeners.forEach { if (it != originator) it(property) }
    }

    fun bindingSetValue(property: KProperty<*>, value: Any?) {
        val anyValue = bindingValueMap[property.name] ?: error("should never be null!")
        anyValue.payload = value
    }

    inline operator fun <reified T : Any?> invoke(default: T? = null): PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>> {

        return PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>> { thisRef, property ->
            val anyValue = bindingValueMap[property.name] ?: default.toAnyValue()
            bindingValueMap[property.name] = anyValue
            object : ReadWriteProperty<Any?, T> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                    return anyValue.payload as T
                }

                override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                    anyValue.payload = value
                    notifyChange(property)
                }
            }
        }
    }

    override fun toString(): String {
        return bindingValueMap.toString()
    }
}
