package databinding

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.jvm.JvmName
import kotlin.reflect.*

inline fun <T, reified F : Function<T>> mapper4(
    constructor: F
) {
    val type = typeOf<F>()
    println("arguments:")
    type.arguments.forEach { println("  type=${it.type}") }
    val invoker = constructor as KFunction4<Any?, Any?, Any?, Any?, T>
    val instance = invoker.invoke(123, 42, "foo", Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
    println("instance = $instance")
}

inline fun <T, reified F : Function4<Any?, Any?, Any?, Any?, T>> funMapper(
    constructor: F
) {
    val type = typeOf<F>()
    println("arguments:")
    type.arguments.forEach { println("  type=${it.type}") }
    val invoker = constructor as KFunction4<Any?, Any?, Any?, Any?, T>
    val instance = invoker.invoke(123, 42, "foo", Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
    println("instance = $instance")
}


inline fun <T, reified F : KFunction<T>> funMapper(constructor: F) = FunMapper<T>()

@JvmName("funMapper1")
inline fun <T, reified F : KFunction1<Any?, T>> funMapper(constructor: F) = FunMapper<T>()

@JvmName("funMapper2")
inline fun <T, reified F : KFunction2<Any?, Any?, T>> funMapper(constructor: F) = FunMapper<T>()

@JvmName("funMapper3")
inline fun <T, reified F : KFunction3<Any?, Any?, Any?, T>> funMapper(constructor: F) = FunMapper<T>()

@JvmName("funMapper4")
inline fun <T, reified F : KFunction4<Any?, Any?, Any?, Any?, T>> funMapper(constructor: F) = FunMapper<T>()

//{
//    val instance = constructor.invoke(123, 42, "foo", Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
//}


class FunMapper<T>() {
    operator fun invoke(): T {
        TODO()
    }
}