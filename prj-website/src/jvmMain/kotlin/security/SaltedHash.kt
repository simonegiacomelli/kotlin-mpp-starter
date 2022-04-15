package security

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

private const val separator = "$"
fun saltedHash(password: String): String {
    val salt = nextSalt()
    val hash = hash(password, salt)
    return listOf(salt, hash).joinToString(separator) { it.toHexString() }
}

fun verifySaltedHash(saltAndHash: String, password: String): Boolean {
    val parts = saltAndHash.split(separator, limit = 2)
    if (parts.size != 2) return false
    val (salt, hash) = parts.map { it.hexStringToByteArray() }
    return verify(password, salt, hash)
}


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

fun ByteArray.toHexString(): String = joinToString(separator = "") { "%02x".format(it) }

fun String.hexStringToByteArray(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }
    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}