package databinding

import serialization.AnyValue
import serialization.toAnyValue
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ChangeListener(val notify: (property: KProperty<*>) -> Unit)


/**
 * cannot be @Serializable! on 2022-04-20 breaks the ir-js compiler!
 * workaround: a custom serializer for every descendant of Bindable
 */
open class Bindable {

    /** this is the problematic property that breaks the compiler */
    val bindingValueMap: LinkedHashMap<String, AnyValue> = LinkedHashMap()

    val bindingListeners = mutableListOf<ChangeListener>()

    fun bindingRegister(changeListener: ChangeListener) {
        bindingListeners.add(changeListener)
    }

    fun bindingSetValueNotify(property: KProperty<*>, value: Any?, originator: ChangeListener? = null) {
        bindingSetValue(property, value)
        notifyChange(property, originator)
    }

    fun notifyChange(property: KProperty<*>, originator: ChangeListener? = null) {
        bindingListeners.forEach { if (it != originator) it.notify(property) }
    }

    fun bindingSetValue(property: KProperty<*>, value: Any?) {
        val anyValue = bindingValueMap[property.name] ?: error("should never be null!")
        anyValue.payload = value
    }

    inline operator fun <reified T : Any?> invoke(default: T? = null): PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>> {
        println("Invoke for default=`$default` mapContent=`$bindingValueMap`")

        return PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>> { thisRef, property ->
            println("PropertyDelegateProvider map=`$bindingValueMap`")
            println("Instantiating a ReadWriteProperty for property name `${property.name}`")
            bindingValueMap[property.name] = bindingValueMap[property.name] ?: default.toAnyValue()
            object : ReadWriteProperty<Any?, T> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                    println("getValue(${property.name})")
                    val anyValue = bindingValueMap[property.name] ?: error("not found!!!!")
                    println("getValue(${property.name}=`${anyValue.payload}`)")
                    val t = anyValue.payload as T
                    println("getValue(${property.name}=`${t}`) casted")
                    return t
                }

                override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                    println("setValue ${property.name}=`$value`")
                    val anyValue = bindingValueMap[property.name] ?: error("not found!!!!") ?: error("not found!!!!")
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
