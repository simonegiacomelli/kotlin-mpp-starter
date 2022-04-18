package pages.bootstrap.databinding

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import widget.Widget
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1

class DataBindingWidget : Widget(//language=HTML
    """
<h1>data binding modeling/prototyping</h1> 

"""
) {

    class User1 : Binding() {
        var name: String by this()
        var age: Int by this()
    }

    private val user1 = User1().apply { name = "simo"; age = 42 }
    private val user3 = User3()

    override fun afterRender() {
        doubleBinding(user1, User1::name) { it::value }
        doubleBinding(user1, User1::age) { IntBridge(it)::value }

        doubleBinding(user3, User3::degree) { it::value }
        doubleBinding(user3, User3::age) { IntBridge(it)::value }
        doubleBinding(user3, User3::birthday) { LocalDateBridge(it)::value }
    }

    fun <E, T> doubleBinding(
        instance: E,
        source: KMutableProperty1<E, T>, bridge: (HTMLInputElement) -> KMutableProperty0<T>
    ) {

        val hw = HelperWidget().apply {
            fun <E, T> InputGroupWidget.bind2(
                instance: E,
                source: KMutableProperty1<E, T>,
                bridge: (HTMLInputElement) -> KMutableProperty0<T>
            ) {
                bind(instance, source, HtmlInputTarget(this.input, bridge(this.input)))
                this.input.oninput = { inputResult.value = instance.toString(); 0 }
                this.addon.innerHTML = source.name
            }

            inputA.bind2(instance, source, bridge)
            inputB.bind2(instance, source, bridge)

        }
        container.append(hw.container)
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

class LocalDateBridge(val target: HTMLInputElement) {
    var value: LocalDate?
        get() = runCatching { target.value.toLocalDate() }.run {
            if (isFailure) console.log(exceptionOrNull())
            getOrNull()
        }
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

private class InputGroupWidget : Widget(//language=HTML
    """
<div class="input-group mb-3">
    <span id='addon' class="input-group-text" id="basic-addon1">A</span>
    <input id='input' type="text" class="form-control" placeholder="an integer A" aria-label="Username"
           aria-describedby="basic-addon1">
</div>
"""
) {
    val input: HTMLInputElement by this
    val addon: HTMLElement by this
}

private class HelperWidget : Widget(//language=HTML
    """
<span id='inputA'></span>
<span id='inputB'></span>

<div class="form-floating mb-3">
    <input id='inputResult' type="email" class="form-control" id="floatingInput" placeholder="">
    <label for="floatingInput">A + B =</label>
</div>
"""
) {
    val inputA by this { InputGroupWidget() }
    val inputB by this { InputGroupWidget() }
    val inputResult: HTMLInputElement by this
}