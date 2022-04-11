package config

import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun Properties.booleanProperty(name: String, def: Boolean): ReadOnlyProperty<Any, Boolean> =
    BooleanProperty(this, name, def)

private class BooleanProperty(private val prop: Properties, private val name: String, private val def: Boolean) :
    ReadOnlyProperty<Any?, Boolean> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        val value = prop.getProperty(name) ?: return def
        return kotlin.runCatching { value.toBoolean() }.getOrDefault(def)
    }

}
