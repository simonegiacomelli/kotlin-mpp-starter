package pages.bootstrap.databinding.demo

import databinding.*
import org.w3c.dom.HTMLInputElement
import pages.bootstrap.commonwidgets.InputGroupWidget
import pages.bootstrap.dateinput.setupDateInsert
import widget.Widget
import kotlin.reflect.KMutableProperty1

class TwoWayBindingDemoWidget : Widget(//language=HTML
    """
<h1>data binding modeling/prototyping</h1> 

"""
) {

    class User1 : Bindable() {
        var name: String by this()
        var age: Int by this()
        override fun toString() = "name=$name age=$age valueMap=$bindingValueMap"
    }

    private val user1 = User1().apply { name = "simo"; age = 42 }
    private val user3 = User3()

    override fun afterRender() {
        doubleBinding(user1, User1::name) { StringTarget(it) }
        doubleBinding(user1, User1::age) { IntTarget(it) }

        doubleBinding(user3, User3::degree) { StringTarget(it) }
        doubleBinding(user3, User3::age) { IntTarget(it) }
        doubleBinding(user3, User3::birthday) { it.setupDateInsert(); LocalDateTarget(it) }
    }

    fun <E, T> doubleBinding(
        instance: E,
        source: KMutableProperty1<E, T>, bridge: (HTMLInputElement) -> TargetProperty<T>
    ) {

        val hw = DoubleInputResultWidget().apply {
            fun InputGroupWidget.bind2(
                bridge: (HTMLInputElement) -> TargetProperty<T>
            ) {
                bind(instance, source, bridge(input))
                this.input.oninput = { inputResult.value = instance.toString(); 0 }
                this.addon.innerHTML = source.name
            }

            inputA.bind2(bridge)
            inputB.bind2(bridge)

        }
        container.append(hw.container)
    }


}


