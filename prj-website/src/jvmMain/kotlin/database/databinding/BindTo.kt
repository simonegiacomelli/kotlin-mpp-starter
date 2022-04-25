package database.databinding


import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import kotlin.reflect.KMutableProperty1

interface ColumnBind<E, V> {
    fun databaseToElement(element: E, resultRow: ResultRow)
    val column: Column<*>
}

interface ColumnsMapper<E> {
    fun map(resultRow: ResultRow): E
    fun columns(): List<Column<*>>
}

infix fun <E, V> Column<V>.bindTo(property: KMutableProperty1<E, V>): ColumnBind<E, V> {
    val column = this
    return object : ColumnBind<E, V> {
        override fun databaseToElement(element: E, resultRow: ResultRow) {
            property.set(element, resultRow[column])
        }

        override val column: Column<*> = column
    }
}

@JvmName("bindToEntityID")
infix fun <E, V : Comparable<V>> Column<EntityID<V>>.bindTo(property: KMutableProperty1<E, V>): ColumnBind<E, EntityID<V>> {
    val column = this
    return object : ColumnBind<E, EntityID<V>> {
        override fun databaseToElement(element: E, resultRow: ResultRow) {
            property.set(element, resultRow[column].value)
        }

        override val column: Column<*> = column
    }
}


fun <E> Collection<ColumnBind<E, *>>.toMapper(new: () -> E): ColumnsMapper<E> {
    val cb = this
    return object : ColumnsMapper<E> {
        override fun map(resultRow: ResultRow): E {
            val res = new()
            forEach { it.databaseToElement(res, resultRow) }
            return res
        }

        override fun columns(): List<Column<*>> = cb.map { it.column }
    }
}
