package pages.bootstrap.databinding

import kotlin.reflect.KMutableProperty1

/** https://docs.microsoft.com/en-us/dotnet/api/system.windows.data.binding#remarks */
fun <E, T> bind(instance: E, source: KMutableProperty1<E, T>, target: Target<T>) {
    fun targetGet() = run { target.bridge.get() }
    fun sourceToTarget() = run { target.bridge.set(source.get(instance)) }
    fun targetToSource() = run { source.set(instance, targetGet()) }

    sourceToTarget()

    if (instance is Binding) {
        val changeListener = ChangeListener { if (it.name == source.name) sourceToTarget() }
        instance.bindingRegister(changeListener)
        target.notifyOnChange { instance.bindingSetValueNotify(source, targetGet(), changeListener) }
    } else {
        target.notifyOnChange { targetToSource() }
    }
}