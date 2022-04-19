@file:Suppress("UNCHECKED_CAST")
@file:OptIn(ExperimentalSerializationApi::class)

package discuss.kotlinlang.rpc_poc

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

interface Request<Res : Any>

suspend inline fun <reified Req : Request<Res>, reified Res : Any> Req.send(): Res = TODO()

data class ReifiedSerializer(val serializer: (Any) -> String, val deserializer: (String) -> Any)
data class TwinSerializer(val requestSerializer: ReifiedSerializer, val responseSerializer: ReifiedSerializer)

inline fun <reified Req, reified Res> twinSerializer() = TwinSerializer(serializers<Req>(), serializers<Res>())

inline fun <reified T> serializers(): ReifiedSerializer = ReifiedSerializer(
    serializer = { instance: T -> Json.encodeToString(instance) } as (Any) -> String,
    deserializer = { json: String -> Json.decodeFromString<T>(json) } as (String) -> Any
)

fun nameOf(klass: KClass<*>): String = klass.simpleName ?: error("simpleName not available")

class Handler(val name: String, val handler: (Any) -> Any, val TwinSerializer: TwinSerializer)

class Handlers {
    val handlers = mutableMapOf<String, Handler>()
    inline fun <reified Req : Request<Res>, reified Res : Any> registerHandler(
        noinline handler: (Req) -> Res,
    ) {
        val name = nameOf(Req::class)
        if (handlers.containsKey(name)) error("Handler named `$name` already registerd")
        handlers[name] = Handler(name, handler as (Any) -> Any, twinSerializer<Req, Res>())
    }
}

@Serializable
data class SumRequest(val a: Int, val b: Int) : Request<SumResponse>

@Serializable
data class SumResponse(val sum: Int)

fun main() {
    val handlers = Handlers()
    handlers.registerHandler { request: SumRequest -> SumResponse(request.a + request.b) }
    handlers.registerHandler { request: SumRequest -> Any() }
//    handlers.registerHandler<SumRequest, SumResponse> { request: SumRequest -> Any() }
    handlers.registerHandler<SumRequest, SumResponse> { request: SumRequest -> SumResponse(request.a + request.b) }
}