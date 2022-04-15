package cli

import api.names.UserCredential
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CliTest {

    @Test
    fun test_noArgs() {
        var called = false
        cli { no_arguments = { called = true } }
        assertTrue(called)
    }

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
        var credential: UserCredential? = null
        cli("user", "passwd", "foo", "secret") { user_passwd = { credential = it } }
        checkNotNull(credential)
        credential?.apply {
            assertEquals("foo", username)
            assertEquals("secret", password)
        }

    }

}