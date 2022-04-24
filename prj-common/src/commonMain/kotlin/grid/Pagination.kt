package grid

import kotlinx.serialization.Serializable

@Serializable
data class Pagination(val index: Int = 0, val itemsPerPage: Int = 50)

fun Pagination?.nextPage(): Pagination {
    if (this == null) return Pagination()
    return copy(index = index + 1)
}

fun Pagination.firstPage() = copy(index = 0)
val Pagination.isFirstPage get() = index == 0