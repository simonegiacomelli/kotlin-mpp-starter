package databinding

import kotlin.test.Test
import kotlin.test.assertEquals

class BindableTest {

    private class Trg(var target_age: Int, var target_name: String = "")

    @Test
    fun test_oneWay_noChangeNotifier() {
        val source = ReadonlyWithBackingField()
        val target = Trg(0)
        val ageBinder = bind(target::target_age, source::age)

        // verify initial copy
        assertEquals(42, target.target_age)
        assertEquals(Mode.OneWay, ageBinder.mode)

        // verify manual copy
        source.values["age"] = 11
        ageBinder.writeTarget()
        assertEquals(11, target.target_age)
    }

    private class ReadonlyWithBackingField {
        val values = mutableMapOf<String, Any?>("age" to 42, "name" to "foo")
        val age: Int by values
        val name: String by values
    }

    @Test
    fun test_oneWay_withChangeNotifier() {
        val changesNotifier = ChangesNotifierDc()
        val source = ReadonlyWithBackingField()
        val target = Trg(0)
        val ageBinder = bind(target::target_age, source::age, changesNotifier)
        // verify initial copy and mode
        assertEquals(42, target.target_age)
        assertEquals(Mode.OneWay, ageBinder.mode)

        val nameBinder = bind(target::target_name, source::name, changesNotifier)
        // verify initial copy and mode
        assertEquals("foo", target.target_name)
        assertEquals(Mode.OneWay, nameBinder.mode)

        source.values["age"] = 11
        source.values["name"] = "bar"

        // notify only age
        changesNotifier.notifyChange(PropertyChangedEventArgs(null, "age"))

        assertEquals(11, target.target_age)
        assertEquals("foo", target.target_name)

        source.values["age"] = 12
        source.values["name"] = "baz"

        // notify all
        changesNotifier.notifyChange(PropertyChangedEventArgs(null, null))
        assertEquals(12, target.target_age)
        assertEquals("baz", target.target_name)
    }


}
