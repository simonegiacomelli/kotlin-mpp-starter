package databinding

import org.jetbrains.exposed.sql.Column
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.typeOf
import kotlin.test.Test

class MapperTest {
    @Test
    fun test_mapper() {
//        val association = mapOf(
//            TmEvent::id to tm_events.id,
//            TmEvent::type_id to tm_events.type_id,
//            TmEvent::arguments to tm_events.arguments,
//            TmEvent::created_at to tm_events.created_at,
//        )
//        val mapper = mapper3(association, ::TmEvent)
    }
}


inline fun <T, reified F : KFunction<T>> mapper3(
    association: Map<KProperty1<T, out Comparable<*>>, Column<out Comparable<*>>>,
    kFunction4: F
): Unit {
    val type = typeOf<F>()
}
