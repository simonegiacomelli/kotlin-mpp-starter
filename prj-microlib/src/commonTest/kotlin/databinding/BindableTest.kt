package databinding

import kotlin.test.Test
import kotlin.test.assertEquals

class BindableTest {

    @Test
    fun test_bindable_1() {
        class Src(val age: Int)
        class Trg(var age: Int)

        val source = Src(42)
        val target = Trg(0)
        bind(source::age, target::age)
        assertEquals(42, target.age)
    }

    @Test
    fun test_bindable_2() {
        class Src() {
            var backingField = 42
            val age get() = backingField
        }

        class Trg(var age: Int)

        val source = Src()
        val target = Trg(0)
        val binder = bind(source::age, target::age)
        assertEquals(42, target.age)
        source.backingField = 11
        binder.writeTarget()
        assertEquals(11, target.age)
    }


}
//
//inline fun <reified V> BindableMnNnRn<V>.toTarget():Binding{
//
//}

