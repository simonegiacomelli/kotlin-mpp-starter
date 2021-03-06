package security

import java.security.SecureRandom

private const val AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
private val rnd = SecureRandom()

fun randomString(len: Int): String {
    val sb = StringBuilder(len)
    repeat(len) { sb.append(AB[rnd.nextInt(AB.length)]) }
    return sb.toString()
}

object SecureToken {


    @JvmStatic
    fun main(args: Array<String>) {
        println(randomString(30))
    }
}