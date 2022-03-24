package utils

fun assert(str: String) = Assert(str)

class Assert(private val str: String, val notEnabled: Boolean = false) {
    val not by lazy { Assert(str, true) }
    fun contains(list: List<String>) = contains(*list.toTypedArray())
    fun contains(vararg list: String) {
        val bad = list.filter { str.contains(it).xor(!notEnabled) }
        if (bad.isEmpty()) return
        val conj = if (!notEnabled) " not" else ""
        error("failed because string [$str] do$conj contains $bad")
    }
}

