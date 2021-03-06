package pages.bootstrap.databinding.demo

import databinding.*
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDate
import pages.bootstrap.commonwidgets.InputGroupWidget
import pages.bootstrap.dateinput.setupDateInsert
import widget.Widget

class DataBindingDemoWidget : Widget(//language=HTML
    """
<h1>data binding demo</h1> 
<div id='div1'></div>
<div id='div2'></div>
<div id='div3'></div>


"""
) {

    private val div1 by this { InputGroupWidget() }
    private val div2 by this { InputGroupWidget() }
    private val div3 by this { InputGroupWidget() }

    class User1 : Bindable() {
        var name: String by this()
        var age: Int by this()
        var birthday: LocalDate? by this()
    }

    private val user1 = User1().apply { name = "simo"; age = 42; birthday = "2022-01-31".toLocalDate() }

    override fun afterRender() {
        bind(user1, User1::name, StringTarget(div1.input))
        bind(user1, User1::age, IntTarget(div2.input))
        bind(user1, User1::birthday, LocalDateTarget(div3.input.also { it.setupDateInsert() }))

        div1.addon.onclick = { user1.name += "."; 0 }
        div2.addon.onclick = { user1.age += 1; 0 }
        div3.addon.onclick = {
            val b = user1.birthday
            if (b != null) user1.birthday = b + DatePeriod(days = 3) else user1.birthday = "1977-01-18".toLocalDate()
            0
        }
    }


}

