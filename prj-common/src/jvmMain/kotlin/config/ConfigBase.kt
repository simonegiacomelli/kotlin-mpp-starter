package config

import java.util.*
import kotlin.reflect.KProperty

open class ConfigBase {

    val prop: Properties = Properties()

    inner class BooleanProp(private val name: String, private val def: Boolean) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            val value = prop.getProperty(name) ?: return def
            return kotlin.runCatching { value.toBoolean() }.getOrDefault(def)
        }

    }

    inner class StringProp(private val name: String, private val def: String) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            val value = prop.getProperty(name) ?: return def
            return kotlin.runCatching { value }.getOrDefault(def)
        }

    }

}