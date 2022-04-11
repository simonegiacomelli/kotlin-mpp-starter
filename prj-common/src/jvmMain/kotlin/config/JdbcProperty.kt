package config

import jdbc.Jdbc
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun Properties.jdbcProperty(name: String): ReadOnlyProperty<Any?, Jdbc> =
    JdbcProperty(this, name)

private class JdbcProperty(private val prop: Properties, private val name: String) :
    ReadOnlyProperty<Any?, Jdbc> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Jdbc {
        val def = Jdbc("", "", "", "", "")
        return kotlin.runCatching { Jdbc.load(prop, name) }.getOrDefault(def)
    }
}