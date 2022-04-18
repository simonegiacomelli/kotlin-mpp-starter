package recursion

/**
 * Restituisce una Map<identificativo-padre,List<elementi-figli>>
 * La lambda viene utilizzata su ogni elemento della lista per estrarne
 * l'identificativo padre.
 * Tale lambda deve restituire null quando un elemento e' radice (non ha padre)
 */
fun <Identifier, E> Set<E>.buildChildrenMap(parentIdentifier: (E) -> Identifier?): Map<Identifier?, MutableList<E>> {
    val elements = this
    return buildMap {
        fun appendChild(element: E) = getOrPut(parentIdentifier(element)) { mutableListOf() }.add(element)
        elements.forEach { element -> appendChild(element) }
    }
}

