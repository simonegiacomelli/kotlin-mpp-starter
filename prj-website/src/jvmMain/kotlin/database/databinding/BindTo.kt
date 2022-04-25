package database.databinding


import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import kotlin.reflect.KMutableProperty1

interface ColumnBind<E> {
    fun databaseToElement(element: E, resultRow: ResultRow)
}

interface ColumnsMapper<E> {
    fun map(resultRow: ResultRow): E
}

infix fun <E, V> Column<V>.bindTo(property: KMutableProperty1<E, V>): ColumnBind<E> {
    val column = this
    return object : ColumnBind<E> {
        override fun databaseToElement(element: E, resultRow: ResultRow) {
            property.set(element, resultRow[column])
        }
    }
}

@JvmName("bindToEntityID")
infix fun <E, V : Comparable<V>> Column<EntityID<V>>.bindTo(property: KMutableProperty1<E, V>): ColumnBind<E> {
    val column = this
    return object : ColumnBind<E> {
        override fun databaseToElement(element: E, resultRow: ResultRow) {
            property.set(element, resultRow[column].value)
        }
    }
}


fun <E> Collection<ColumnBind<E>>.toMapper(new: () -> E): ColumnsMapper<E> = object : ColumnsMapper<E> {
    override fun map(resultRow: ResultRow): E {
        val res = new()
        forEach { it.databaseToElement(res, resultRow) }
        return res
    }
}
