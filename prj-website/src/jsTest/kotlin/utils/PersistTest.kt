package widget.utils

import extensions.input
import kotlinx.browser.localStorage
import org.w3c.dom.set
import utils.Persist
import kotlin.test.Test
import kotlin.test.assertEquals

class PersistTest {
    @Test
    fun html_input() {
        val input1 = input { value = "should-be-saved" }
        Persist("test1").add(input1, "input1").save()

        val input2 = input { value = "ciao2" }
        Persist("test1").add(input2, "input1").load()

        assertEquals("should-be-saved", input2.value)
    }

    @Test
    fun html_input_checkbox() {

        Persist("test1").add(input { type = "checkbox"; checked = true }, "input1").save()

        val input2 = input { checked = false }
        Persist("test1").add(input2, "input1").load()

        assertEquals(true, input2.checked)
    }

    @Test
    fun no_exceptions() {

        val input1 = input { value = "should-not-change" }
        val target = Persist("test1").add(input1, "input1")

        // given
        localStorage["test1"] = "broken!"

        // when
        target.load()

        // then
        assertEquals("should-not-change", input1.value)

        // given
        localStorage["test1"] = """{"input1":"broken!"}"""

        // when
        target.load()

        // then
        assertEquals("should-not-change", input1.value)
    }

}