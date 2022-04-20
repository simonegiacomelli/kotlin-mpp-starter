package databinding

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

/** naming from https://docs.microsoft.com/en-us/dotnet/api/system.windows.data.binding#remarks */
fun <E, T> bind(sourceInstance: E, sourceProperty: KMutableProperty1<E, T>, target: Target<T>) {
    fun targetGet() = run { target.property.get() }
    fun sourceGet() = run { sourceProperty.get(sourceInstance) }
    fun changed() = (targetGet() != sourceGet()).also { println("prop-old:${sourceProperty.name} changed:" + it) }
    fun sourceToTarget() = run { if (changed()) target.property.set(sourceGet()) }
    fun targetToSource() = run { if (changed()) sourceProperty.set(sourceInstance, targetGet()) }

    sourceToTarget()

    if (sourceInstance is InternallyChangeable) {
        val listener: (property: KProperty<*>) -> Unit = { if (it.name == sourceProperty.name) sourceToTarget() }
        sourceInstance.onChange(listener)
        if (sourceInstance is ExternallyChangeable)
            target.onChange { sourceInstance.change(sourceProperty, targetGet()) }
    } else {
        target.onChange { targetToSource() }
    }
}

fun <S, T, P> bind(
    sourceInstance: S, sourceProperty: KMutableProperty1<S, P>,
    targetInstance: T, targetProperty: KMutableProperty0<P>
) {

    fun targetGet() = run { targetProperty.get() }
    fun sourceGet() = run { sourceProperty.get(sourceInstance) }
    fun changed() = (targetGet() != sourceGet()).also { println("prop-new:${sourceProperty.name} changed:" + it) }

    fun sourceToTarget() = run { if (changed()) targetProperty.set(sourceGet()) }
    fun targetToSource() = run { if (changed()) sourceProperty.set(sourceInstance, targetGet()) }

    sourceToTarget()

    if (sourceInstance is InternallyChangeable) {
        val listener: (property: KProperty<*>) -> Unit = { if (it.name == sourceProperty.name) sourceToTarget() }
        sourceInstance.onChange(listener)
        if (sourceInstance is ExternallyChangeable && targetInstance is InternallyChangeable)
            targetInstance.onChange { sourceInstance.change(sourceProperty, targetGet()) }
    } else {
        if (targetInstance is InternallyChangeable)
            targetInstance.onChange { targetToSource() }
    }
}

