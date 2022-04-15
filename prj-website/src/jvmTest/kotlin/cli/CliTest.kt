package cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CliTest {

    @Test
    fun test_startKtor() {
        var called = false
        cli("start") { start = { called = true } }
        assertTrue(called)
    }

    @Test
    fun test_user_create() {
        var str = ""
        cli("user", "create", "foo") { user_create = { str = it } }
        assertEquals("foo", str)
    }

    @Test
    fun test_user_passwd() {
        var str = ""
        cli("user", "passwd", "foo") { user_passwd = { str = it } }
        assertEquals("foo", str)
    }

}