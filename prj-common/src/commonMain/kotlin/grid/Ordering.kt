package grid

import kotlinx.serialization.Serializable

@Serializable
data class Ordering(val propertyName: String, val ascending: Boolean)

fun Ordering?.loop(columnName: String): Ordering? {
    return when {
        this == null || sameColumn(columnName) -> Ordering(columnName, true)
        !ascending -> null
        else -> copy(ascending = !ascending)
    }
}

private fun Ordering.sameColumn(columnName: String) = propertyName != columnName
