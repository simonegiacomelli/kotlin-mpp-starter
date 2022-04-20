package databinding

import kotlin.reflect.KProperty

interface ExternallyChangeable {
    /** this will change the property value and will notify
     * registered listeners */
    fun change(property: KProperty<*>, value: Any?)
}