package cli

import api.names.Credential
import rpc.server.UserRoleStr
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
        var credential: Credential? = null
        cli("user", "passwd", "foo", "secret") { user_passwd = { credential = it } }
        checkNotNull(credential)
        credential?.apply {
            assertEquals("foo", username)
            assertEquals("secret", password)
        }

    }

    @Test
    fun test_user_add_role() {
        var role: UserRoleStr? = null
        cli("user", "add-role", "foo", "role1") { user_add_role = { role = it } }
        checkNotNull(role)
        role?.apply {
            assertEquals("foo", username)
            assertEquals("role1", roleName)
        }

    }

    @Test
    fun test_user_remove_role() {
        var role: UserRoleStr? = null
        cli("user", "remove-role", "foo", "role1") { user_remove_role = { role = it } }
        checkNotNull(role)
        role?.apply {
            assertEquals("foo", username)
            assertEquals("role1", roleName)
        }

    }

}