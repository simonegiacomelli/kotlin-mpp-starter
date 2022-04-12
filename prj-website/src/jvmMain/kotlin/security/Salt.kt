package security

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Salt {
    private val random = SecureRandom()

    fun nextSalt() = ByteArray(16).apply { random.nextBytes(this) }

    fun verify(password: String, salt: ByteArray, expectedHash: ByteArray): Boolean {
        val pwdHash = hash(password, salt)
        if (pwdHash.size != expectedHash.size) return false
        return pwdHash.indices.all { pwdHash[it] == expectedHash[it] }
    }

    fun hash(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, 1000, 256)
        try {
            val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            return skf.generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val salt = nextSalt()
        val hash = hash("secret1", salt)
        verify("secret1", salt, hash).also { println(it) }
        verify("secret2", salt, hash).also { println(it) }
    }
}