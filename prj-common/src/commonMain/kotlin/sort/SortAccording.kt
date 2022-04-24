package sort

fun <E> List<E>.sortAccording(to: List<String>, name: (E) -> String): List<E> {
    val src = groupBy(name).toMutableMap()
    val ordered1 = to.mapNotNull { src.remove(it) }.flatten()
    val toSet = to.toSet()
    val remaining = filterNot { toSet.contains(name(it)) }
    return ordered1 + remaining
}