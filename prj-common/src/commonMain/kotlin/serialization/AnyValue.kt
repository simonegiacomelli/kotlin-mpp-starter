package serialization

import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable(with = AnyValueSerializer::class)
data class AnyValue(val kClass: KClass<*>, var payload: Any?)

inline fun <reified T : Any?> T.toAnyValue() = AnyValue(T::class, this)