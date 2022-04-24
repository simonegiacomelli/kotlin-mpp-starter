package grid

data class GridPagination(var index: Int = 0, var itemsPerPage: Int = 50)


fun GridPagination.toSerializable() = Pagination(index, itemsPerPage)
