package grid

import kotlin.reflect.KClass

val KClass<*>.simpleNameOrBlank: String get() = simpleName ?: ""

class TreeGridWidget<E> : GridWidget<E>() {

    var onChildrenFor: (parent: E?) -> List<E> = { emptyList() }

    override var sortableHead: Boolean = false
        set(value) {
            if (value) error("La proprieta' `${::sortableHead.name}` non e' supportato da ${TreeGridWidget::class.simpleNameOrBlank}")
            field = false
        }

    private val depthIndexes = mutableListOf<Int>()

    override fun render(): TreeGridWidget<E> = apply {
        depthIndexes.clear()
        elements = buildList { this.recurseDepth(0, null) }
        super.render()
    }

    private fun MutableList<E>.recurseDepth(depth: Int, parent: E?) {
        onChildrenFor(parent).forEach { element ->
            add(element)
            depthIndexes.add(depth)
            recurseDepth(depth + 1, element)
        }
    }

    fun depthFor(index: Int): Int = depthIndexes[index]
    fun depthFor(element: E): Int = elementInfoFor(element).run { depthFor(index) }
}
