package korm

import java.sql.Connection
import java.sql.ResultSet
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3
import kotlin.reflect.KProperty1


class Meta<T>(val tableName: String, val primaryKey: KProperty1<T, *>, val fields: List<KProperty1<T, *>>)

fun <T> Connection.kormInsert(meta: Meta<T>, vararg rows: T) = meta.run {
    val fieldList = buildList { add(primaryKey); addAll(fields) }
    val fieldNameList = fieldList.joinToString(", ") { it.name }
    val argsList = fieldList.joinToString(", ") { "?" }
    val statement = prepareStatement("insert into $tableName ($fieldNameList) values ($argsList) ")
    rows.forEach { row ->
        val values = fieldList.map { it.get(row) }
        values.forEachIndexed { index, any -> statement.setObject(index + 1, any) }
        statement.executeUpdate()
    }
    statement.close()
}


@JvmName("korm2")
inline fun <reified A, reified B, reified Res> ResultSet.korm(
    kFun: KFunction2<A, B, Res>
): Sequence<Res> = sequence {

    while (next()) {
        val a = getObject(1) as A
        val b = getObject(2) as B
        val value: Res = kFun(a, b)
        yield(value)
    }
}

@JvmName("korm3")
inline fun <reified A, reified B, reified C, reified Res> ResultSet.korm(
    kFun: KFunction3<A, B, C, Res>
): Sequence<Res> = sequence {

    while (next()) {
        val a = getObject(1) as A
        val b = getObject(2) as B
        val c = getObject(3) as C
        val value: Res = kFun(a, b, c)
        yield(value)
    }
}