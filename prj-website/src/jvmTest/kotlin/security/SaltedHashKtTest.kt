package security


import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class SaltedHashKtTest {

    @Test
    fun test_saltedHash() {
        val hash = saltedHash("foobar")
        assertTrue(verifySaltedHash(hash, "foobar"))
        assertFalse(verifySaltedHash(hash, "wrong"))
    }
}