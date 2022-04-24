package databinding

import jdbcx.Column
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import telemetry.api.TmEvent
import telemetry.schema.TmEventsSchema
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction4
import kotlin.reflect.KProperty1
import kotlin.reflect.typeOf
import kotlin.test.Test

class MapperTest {
    @Test
    fun test_mapper() {
        val association = mapOf(
            TmEvent::id to TmEventsSchema.id,
            TmEvent::type_id to TmEventsSchema.type_id,
            TmEvent::arguments to TmEventsSchema.arguments,
            TmEvent::created_at to TmEventsSchema.created_at,
        )
//        val mapper = mapper3(association, ::TmEvent)
        val mapper4 = mapper4(::TmEvent)
    }

    @Test
    fun test_dynamicKFunctionInvoke() {
        val mapper4 = funMapper(::TmEvent)
    }
}

inline fun <T, reified F : KFunction<T>> mapper3(
    association: Map<KProperty1<T, out Comparable<*>>, Column>,
    constructor: F
): Unit {
    val type = typeOf<F>()
    println("name=" + constructor.name)
    println("arguments:")
    type.arguments.forEach { println("  type=${it.type}") }
    val invoker = constructor as KFunction4<Any?, Any?, Any?, Any?, T>
    val instance = invoker.invoke(123, 42, "foo", Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
    println("instance = $instance")
}
