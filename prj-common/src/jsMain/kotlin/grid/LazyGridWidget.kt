package grid

import extensions.extVisible
import extensions.tbodyFirst
import kotlinx.dom.clear
import org.w3c.dom.HTMLElement

open class LazyGridWidget<E> : GridWidget<E>(
    postHtml = //language=HTML
    """
    <div id="loadMoreElement" style="display: none">Carica altro...</div>
""".trimIndent()
) {

    val pagination: GridPagination = GridPagination()
    var onLazyLoad: (LazyLoadEvent<E>.() -> Unit)? = null
    val loadMoreElement: HTMLElement by this

    override fun render(): LazyGridWidget<E> = apply {
        beforeRender()
        startLazyRender()
    }

    private fun renderInternal() {
        renderHead()
        val startIndex = pagination.index * pagination.itemsPerPage
        val pageElements = orderElements(elements.drop(startIndex))
        pageElements.forEachIndexed { relativeIndex, element ->
            appendElement(startIndex + relativeIndex, element)
        }
    }

    private fun startLazyRender() {
        val onLazyLoad = onLazyLoad ?: error("L'evento onLazyLoad deve essere gestito")
        pagination.index = 0
        elements = emptyList()
        loadMore(onLazyLoad)
    }

    private fun loadMore(onLazyLoad: LazyLoadEvent<E>.() -> Unit) {
        loadMoreElement.onclick = { loadMore(onLazyLoad) }
        onLazyLoad.invoke(LazyLoadEventDc(this, pagination, loadDoneCallback = ::loadBatchDone))
    }

    private fun loadBatchDone(lazyLoadEvent: LazyLoadEvent<E>) {
        if (pagination.index == 0) table.tbodyFirst().clear()
        elements = elements + lazyLoadEvent.elements
        renderInternal()
        loadMoreElement.extVisible = lazyLoadEvent.elements.size >= pagination.itemsPerPage
        pagination.index++
    }


}