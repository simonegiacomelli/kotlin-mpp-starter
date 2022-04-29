package database.databinding


import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

interface ColumnBind<E, V> {
    fun databaseToElement(element: E, resultRow: ResultRow)
    val column: Column<*>
}

interface ColumnsMapper<E> {
    fun map(resultRow: ResultRow): E
    fun columns(): List<Column<*>>
    fun mapAll(table: Table): ColumnsMapper<E> = TODO()
}

infix fun <E, V> Column<V>.bindTo(property: KMutableProperty1<E, V>): ColumnBind<E, V> {
    val column = this
    return object : ColumnBind<E, V> {
        override fun databaseToElement(element: E, resultRow: ResultRow) {
            val value = resultRow[column]
            property.set(element, value)
        }

        override val column: Column<*> = column
    }
}

@JvmName("bindToEntityID")
infix fun <E, V : Comparable<V>> Column<EntityID<V>>.bindTo(property: KMutableProperty1<E, V>): ColumnBind<E, EntityID<V>> {
    val column = this
    return object : ColumnBind<E, EntityID<V>> {
        override fun databaseToElement(element: E, resultRow: ResultRow) {
            val value = resultRow[column].value
            property.set(element, value)
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


inline fun <reified E : Any> exposedMapper(noinline constructor: () -> E): ColumnsMapper<E> {
    return object : ColumnsMapper<E> {
        override fun map(resultRow: ResultRow): E = constructor()
        override fun columns(): List<Column<*>> = emptyList()
        override fun mapAll(table: Table): ColumnsMapper<E> =
            E::class.memberProperties.map { columnBind(it, table) }.toMapper(constructor)

    }
}

fun <E> columnBind(property: KProperty1<E, *>, table: Table): ColumnBind<E, *> {
    val column = table.columns.first { col -> col.name == property.name }
    val col = column as Column<Any?>
    val prop = property as KMutableProperty1<E, Any?>
    return col.bindTo(prop)
}


