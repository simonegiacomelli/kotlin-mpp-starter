package databinding

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1

/** naming from https://docs.microsoft.com/en-us/dotnet/api/system.windows.data.binding#remarks */
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

    if (targetInstance is InternallyChangeable)
        targetInstance.onChange { targetToSource() }

    if (sourceInstance is InternallyChangeable) {
        sourceInstance.onChange { if (it.name == sourceProperty.name) sourceToTarget() }
    }
}


fun <S, P> bind(
    sourceInstance: S, sourceProperty: KMutableProperty1<S, P>,
    target: PropertyBridge<P>
) = bind(sourceInstance, sourceProperty, target, target::value)
