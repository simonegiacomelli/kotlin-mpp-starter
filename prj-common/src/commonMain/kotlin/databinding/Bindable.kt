package databinding

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import serialization.AnyValue
import serialization.toAnyValue
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ChangeListener(val notify: (property: KProperty<*>) -> Unit)

@Serializable
open class Bindable {

    val bindingValueMap = mutableMapOf<String, AnyValue>()

    @Transient
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
            val anyValue = bindingValueMap[property.name] ?: default.toAnyValue()
            println("Instantiating a ReadWriteProperty for property name `${property.name}`")
            bindingValueMap[property.name] = anyValue
            object : ReadWriteProperty<Any?, T> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                    return anyValue.payload as T
                }

                override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                    println("setValue ${property.name}=`$value`")
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
