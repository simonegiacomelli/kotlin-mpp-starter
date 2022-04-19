package pages.bootstrap.databinding.demo

import org.w3c.dom.HTMLInputElement
import pages.bootstrap.databinding.*
import widget.Widget
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1

class DataBindingWidget : Widget(//language=HTML
    """
<h1>data binding modeling/prototyping</h1> 

"""
) {

    class User1 : Bindable() {
        var name: String by this()
        var age: Int by this()
    }

    private val user1 = User1().apply { name = "simo"; age = 42 }
    private val user3 = User3()

    override fun afterRender() {
        doubleBinding(user1, User1::name) { stringBridge(it) }
        doubleBinding(user1, User1::age) { intBridge(it) }

        doubleBinding(user3, User3::degree) { stringBridge(it) }
        doubleBinding(user3, User3::age) { intBridge(it) }
        doubleBinding(user3, User3::birthday) { localDateBridge(it) }
    }

    fun <E, T> doubleBinding(
        instance: E,
        source: KMutableProperty1<E, T>, bridge: (HTMLInputElement) -> KMutableProperty0<T>
    ) {

        val hw = DoubleInputResultWidget().apply {
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


}


