package config

import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun Properties.stringProperty(name: String, def: String): ReadOnlyProperty<Any, String> =
    StringProperty(this, name, def)

class StringProperty(private val prop: Properties, private val name: String, private val def: String) :
    ReadOnlyProperty<Any?, String> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        val value = prop.getProperty(name) ?: return def
        return kotlin.runCatching { value }.getOrDefault(def)
    }
}