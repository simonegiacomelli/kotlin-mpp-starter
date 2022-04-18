package pages.bootstrap.databinding

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ChangeListener(val notify: (property: KProperty<*>) -> Unit)

open class Binding {
    val bindingValueMap = mutableMapOf<String, Any?>()
    val bindingListeners = mutableListOf<ChangeListener>()

    fun bindingRegister(changeListener: ChangeListener) {
        bindingListeners.add(changeListener)
    }

    fun bindingSetValueNotify(property: KProperty<*>, value: Any?, originator: ChangeListener? = null) {
        bindingSetValue(property, value)
        bindingListeners.forEach { if (it != originator) it.notify(property) }
    }

    fun bindingSetValue(property: KProperty<*>, value: Any?) {
        bindingValueMap[property.name] = value
    }

    inline operator fun <reified T> invoke(default: T): PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>> {
//        var field: T = default

        return PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>> { thisRef, property ->
            bindingSetValue(property, default)
            object : ReadWriteProperty<Any?, T> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                    val field = bindingValueMap[property.name]
                    console.log("getValue($field)")
                    return field as T
                }

                override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                    console.log("setValue($value)")
                    bindingSetValueNotify(property, value)
//                    field = value
                }
            }
        }
    }


}