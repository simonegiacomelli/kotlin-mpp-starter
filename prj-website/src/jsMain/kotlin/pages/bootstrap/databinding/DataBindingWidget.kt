package pages.bootstrap.databinding

import kotlinx.serialization.Transient
import org.w3c.dom.HTMLInputElement
import widget.Widget
import kotlin.properties.Delegates
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

class DataBindingWidget : Widget(//language=HTML
    """
<h1>data binding modeling/prototyping</h1> 
<div class="input-group mb-3">
    <span class="input-group-text" id="basic-addon1">A</span>
    <input id='inputA' type="text" class="form-control" placeholder="an integer A" aria-label="Username"
           aria-describedby="basic-addon1">
</div>

<div class="input-group mb-3">
    <span class="input-group-text" id="basic-addon1">B</span>
    <input id='inputB' type="text" class="form-control" placeholder="an integer B" aria-label="Username"
           aria-describedby="basic-addon1">
</div>

<div class="form-floating mb-3">
    <input id='inputResult' type="email" class="form-control" id="floatingInput" placeholder="">
    <label for="floatingInput">A + B =</label>
</div>
"""
) {
    private val inputA: HTMLInputElement by this
    private val inputB: HTMLInputElement by this
    private val inputResult: HTMLInputElement by this

    @kotlinx.serialization.Serializable
    class User {
        @Transient
        val map: MutableMap<String, Any?> = mutableMapOf()

        @Transient
        private val provider = PropertyDelegateProvider { thisRef: Any?, property ->
            object : ReadWriteProperty<Any?, Int> {
                val name = property.name

                override fun getValue(thisRef: Any?, property: KProperty<*>): Int = (map[name] ?: 42) as Int
                override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
                    map[name] = value
                }
            }
        }
        var name: String by Delegates.observable("<no name>") { prop, old, new ->
            println("$old -> $new")
        }
        var age: Int by provider
        var color: String = "red"
    }

    private val user3 = User3()

    override fun afterRender() {

//        bind(user3, User3::age, inputA)
//        bind(user3, User3::age, inputB)
//        bind(user3, User3::degree, inputB)

//        bind(user3, User3::degree, HtmlInputTarget(inputA, inputA::value))
//        bind(user3, User3::degree, HtmlInputTarget(inputB, inputB::value))
        bind(user3, User3::age, HtmlInputTarget(inputA, IntBridge(inputA)::value))
        bind(user3, User3::age, HtmlInputTarget(inputB, IntBridge(inputB)::value))

        inputA.oninput = { inputResult.value = user3.toString(); 0 }
        inputB.oninput = { inputResult.value = user3.toString(); 0 }
    }


    private fun <E> bind2(instance: E, kname: KMutableProperty1<E, String>, inputA: HTMLInputElement) {
        fun propToGui() = run { inputA.value = kname.get(instance) }
        fun guiToProp() = run { kname.set(instance, inputA.value) }
        propToGui()
        inputA.addEventListener("input", { guiToProp() })
    }

    private fun <E> bind2(instance: E, prop: KMutableProperty1<E, Int>, htmlInputElement: HTMLInputElement) {
        fun guiValue() = run { htmlInputElement.value.toIntOrNull() ?: -1 }
        fun propToGui() = run { htmlInputElement.value = prop.get(instance).toString() }
        fun guiToProp() = run { prop.set(instance, guiValue()) }
        propToGui()

        if (instance is Binding) {
            val changeListener = ChangeListener { if (it.name == prop.name) propToGui() }
            instance.bindingRegister(changeListener)
            htmlInputElement.addEventListener("input",
                { instance.bindingSetValueNotify(prop, guiValue(), changeListener) })
        } else {
            htmlInputElement.addEventListener("input", { guiToProp() })
        }
    }

    //https://docs.microsoft.com/en-us/dotnet/api/system.windows.data.binding#remarks
    private fun <E, T> bind(instance: E, source: KMutableProperty1<E, T>, target: Target<T>) {
        fun guiValue() = run { target.propertyBridge.get() }
        fun propToGui() = run { target.propertyBridge.set(source.get(instance)) }
        fun guiToProp() = run { source.set(instance, guiValue()) }
        propToGui()

        if (instance is Binding) {
            val changeListener = ChangeListener { if (it.name == source.name) propToGui() }
            instance.bindingRegister(changeListener)
            target.notifyOnChange { instance.bindingSetValueNotify(source, guiValue(), changeListener) }
        } else {
            target.notifyOnChange { guiToProp() }
        }
    }
}

interface Target<T> {
    val propertyBridge: KMutableProperty0<T>
    fun notifyOnChange(listener: () -> Unit)
}

class IntBridge(val target: HTMLInputElement) {
    var value: Int
        get() = target.value.toIntOrNull() ?: -1
        set(value) = run { target.value = "$value" }
}

class HtmlInputTarget<T>(
    val target: HTMLInputElement,
    override val propertyBridge: KMutableProperty0<T>
) : Target<T> {

    override fun notifyOnChange(listener: () -> Unit) {
        target.addEventListener("input", { listener() })
    }

//    override val property: KMutableProperty1<HTMLInputElement, String> = HTMLInputElement::value
}
