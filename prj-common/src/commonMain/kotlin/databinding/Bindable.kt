package databinding

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ChangeListener(val notify: (property: KProperty<*>) -> Unit)

open class Bindable {
    val bindingValueMap = mutableMapOf<String, Optional<Any?>>()
    val bindingListeners = mutableListOf<ChangeListener>()

    fun bindingRegister(changeListener: ChangeListener) {
        bindingListeners.add(changeListener)
    }

    fun bindingSetValueNotify(property: KProperty<*>, value: Any?, originator: ChangeListener? = null) {
        bindingSetValueNotify(property, valueOf(value), originator)
    }

    fun bindingSetValueNotify(property: KProperty<*>, value: Optional<Any?>, originator: ChangeListener? = null) {
        bindingSetValue(property, value)
        bindingListeners.forEach { if (it != originator) it.notify(property) }
    }

    fun bindingSetValue(property: KProperty<*>, value: Optional<Any?>) {
        bindingValueMap[property.name] = value
    }

    inline operator fun <reified T> invoke(default: T? = null): PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>> {
        val optional = if (default == null) empty() else valueOf(default)
//        var field: T = default
        return PropertyDelegateProvider<Any?, ReadWriteProperty<Any?, T>> { thisRef, property ->
            bindingSetValue(property, optional as Optional<Any?>)
            object : ReadWriteProperty<Any?, T> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                    val field = bindingValueMap[property.name]
//                    console.log("getValue($field)")
                    checkNotNull(field) { "should not be null" }
                    if (!field.empty) error("no value set for property. You could provide a default value")
                    return field.value as T
                }

                override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
//                    console.log("setValue($value)")
                    bindingSetValueNotify(property, value)
//                    field = value
                }
            }
        }
    }

    override fun toString(): String {
        return bindingValueMap.toString()
    }
}
