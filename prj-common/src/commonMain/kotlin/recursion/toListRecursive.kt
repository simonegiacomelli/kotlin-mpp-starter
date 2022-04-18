package recursion

fun <T> Collection<T>.toListRecursive(
    accept: (T) -> Boolean = { true },
    inspect: (T) -> Boolean = { true },
    children: (T) -> Collection<T>
): List<T> {
    val res = mutableListOf<T>()

    fun recurse(collection: Collection<T>) {
        collection.forEach { if (accept(it)) res.add(it) }
        collection.forEach { if (inspect(it)) recurse(children(it)) }
    }

    recurse(this)
    return res
}
