package rpc

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


data class AnySerializer(val serializer: (Any) -> String, val deserializer: (String) -> Any)

class MissingHandler(msg: String) : Throwable(msg)

inline fun <reified T> serializers(): AnySerializer = AnySerializer(
    serializer = { instance: T -> Json.encodeToString(instance) } as (Any) -> String,
    deserializer = { json: String -> Json.decodeFromString<T>(json) } as (String) -> Any
)


