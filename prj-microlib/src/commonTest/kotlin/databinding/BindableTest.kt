package databinding

import kotlin.test.Test
import kotlin.test.assertEquals

class BindableTest {

    class Trg(var age: Int)

    @Test
    fun test_readonlySource_toWritableTarget_initialCopy() {
        class Src(val age: Int)

        val source = Src(42)
        val target = Trg(0)
        bind(source::age, target::age)
        assertEquals(42, target.age)
    }

    @Test
    fun test_readonlySource_toWritableTarget_manualCopy() {
        class Src() {
            var backingField = 42
            val age get() = backingField
        }

        val source = Src()
        val target = Trg(0)
        val binder = bind(source::age, target::age)
        assertEquals(42, target.age)
        source.backingField = 11
        binder.writeTarget()
        assertEquals(11, target.age)
    }


    @Test
    fun test_readonlySourceWithNotification_toWritableTarget_automaticCopy() {
        class Src {
            var backingField = 42
            val age get() = backingField
        }

        val changesNotifier = ChangesNotifierDc()
        val source = Src()
        val target = Trg(0)
        bind(changesNotifier, source::age, target::age)
        assertEquals(42, target.age)
        source.backingField = 11
        changesNotifier.notifyListeners()
        assertEquals(11, target.age)
    }


}
//
//inline fun <reified V> BindableMnNnRn<V>.toTarget():Binding{
//
//}

