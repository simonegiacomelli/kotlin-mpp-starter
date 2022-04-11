package config

import java.io.StringReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConfigBaseTest {

    class ConfigMock : ConfigBase() {
        companion object {
            operator fun invoke(content: String) =
                ConfigMock().apply { prop.load(StringReader(content)) }
        }

        val foo: Boolean by BooleanProp("foo", false)
        val bar: String by StringProp("bar", "1234")
    }

    @Test
    fun defaultBooleanProperty_shouldBe_false() {
        val target = ConfigMock("")
        assertFalse { target.foo }
    }

    @Test
    fun defaultBooleanProperty() {
        val target = ConfigMock("foo=true")
        assertTrue { target.foo }
    }

    @Test
    fun invalidBooleanProperty_shouldReturn_default() {
        val target = ConfigMock("foo=xyz")
        assertFalse { target.foo }
    }

    @Test
    fun defaultStringProperty() {
        val target = ConfigMock("bar=ciccio")
        assertEquals("ciccio", target.bar)
    }

    @Test
    fun externaLoad() {
        val target = ConfigMock()
        target.prop["bar"] = "external"
        assertEquals("external", target.bar)
    }

}