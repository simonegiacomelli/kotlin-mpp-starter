package databinding

import kotlin.reflect.KMutableProperty1

/** naming from https://docs.microsoft.com/en-us/dotnet/api/system.windows.data.binding#remarks */
fun <E, T> bind(instance: E, source: KMutableProperty1<E, T>, target: Target<T>) {
    fun targetGet() = run { target.bridge.get() }
    fun sourceToTarget() = run { target.bridge.set(source.get(instance)) }
    fun targetToSource() = run { source.set(instance, targetGet()) }

    sourceToTarget()

    if (instance is Bindable) {
//        val changeListener = ChangeListener { if (it.name == source.name) sourceToTarget() }
        val changeListener = instance.onChange { if (it.name == source.name) sourceToTarget() }
        target.onChange { instance.bindingSetValueNotify(source, targetGet(), changeListener) }
    } else {
        target.onChange { targetToSource() }
    }
}