package rpc.server

import api.names.ApiTmNewEventRequest
import database.schema.tm_events
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import rpc.VoidResponse
import telemetry.api.ApiTmListEventsRequest
import telemetry.api.ApiTmListEventsResponse
import telemetry.api.TmEvent
import telemetry.newEvent
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction4
import kotlin.reflect.KProperty1
import kotlin.reflect.typeOf

private val reg1 = contextHandler.register { req: ApiTmNewEventRequest, _ ->
    transaction { newEvent(db, req.type_id, req.arguments) }
    VoidResponse
}

private val reg2 = contextHandler.register { req: ApiTmListEventsRequest, _ ->
    val association = mapOf(
        TmEvent::id to tm_events.id,
        TmEvent::type_id to tm_events.type_id,
        TmEvent::arguments to tm_events.arguments,
        TmEvent::created_at to tm_events.created_at,
    )
    val map = funMapperL4(::TmEvent, tm_events.id, tm_events.type_id, tm_events.arguments, tm_events.created_at)
    val events = transaction {
        tm_events.selectAll().orderBy(tm_events.id, SortOrder.DESC).limit(50).map {
            map(it)
//            TmEvent(
//                it[tm_events.id].value,
//                it[tm_events.type_id],
//                it[tm_events.arguments],
//                it[tm_events.created_at],
//            )
        }
    }
    ApiTmListEventsResponse(events)
}


inline fun <T, reified F : KFunction<T>> mapper2(
    constructor: F,
    vararg exposedColumns: Column<*>
): (ResultRow) -> T {
    val type = typeOf<F>()
    val parameterCount = type.arguments.size - 1
    if (parameterCount != 4) error("Number of parameter $parameterCount not supported (easy to add)")


    val columnCount = exposedColumns.size
    if (parameterCount != columnCount)
        error("The constructor parameter count do not match the number of columns specified: parameterCount=$parameterCount columnCount=$columnCount")

    val invoker = constructor as KFunction4<Any?, Any?, Any?, Any?, T>
    val (c0, c1, c2, c3) = exposedColumns
    return { r: ResultRow -> invoker(r[c0], r[c1], r[c2], r[c3]) }
}


inline fun <T, reified F : KFunction<T>> mapper(
    association: Map<KProperty1<T, Comparable<*>>, Column<*>>,
    constructor: F
): (ResultRow) -> T {
    val type = typeOf<F>()
    println("name=" + constructor.name)
    println("arguments:")
    type.arguments.forEach { println("  type=${it.type}") }
    val invoker = constructor as KFunction4<Any?, Any?, Any?, Any?, T>
    val instance = invoker.invoke(123, 42, "foo", Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
    println("instance = $instance")
    TODO()
}


inline fun <T, reified F : KFunction4<*, *, *, *, T>> funMapperL4(
    constructor: F,
    c0: Column<*>,
    c1: Column<*>,
    c2: Column<*>,
    c3: Column<*>
)
        : (ResultRow) -> T {
    val c = constructor as KFunction4<Any?, Any?, Any?, Any?, T>
//    val (c0, c1, c2, c3) = exposedColumns
//    println("c0 is DaoEntityID<*> =" +(c0 is EntityID<*>))
    println("istype=" + (c0.columnType is EntityIDColumnType<*>))
    val c0a = c0 as Column<EntityID<*>>

    return { r: ResultRow -> c(r[c0a].value, r[c1], r[c2], r[c3]) }
}
