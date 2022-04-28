package rpc

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ContextHandlers<Context> {


    data class ContextHandler<Context>(
        val requestSerializer: AnySerializer,
        val responseSerializer: AnySerializer,
        val handler: (Any, Context) -> Any,
    )

    val handlers = mutableMapOf<String, ContextHandler<Context>>()

    inline fun <reified Req : Request<Resp>, reified Resp> register(
        noinline function: (Req, Context) -> Resp,
    ) {
        val handlerName = Req::class.simpleName ?: error("no class name")
        if (handlers.containsKey(handlerName)) error("Twoway handler already registered: $handlerName")
        val handler = ContextHandler(
            requestSerializer = serializers<Req>(),
            responseSerializer = serializers<Resp>(),
            handler = function as (Any, Context) -> Any
        )


        handlers[handlerName] = handler

        println("Twoway registered  $handlerName")
    }

    fun dispatch(payload: String, context: Context) = dispatch(RpcMessage.decode(payload), context)
    fun dispatch(m: RpcMessage, context: Context) = dispatch(m.name, m.payload, context)
    fun dispatch(simpleName: String, payload: String, context: Context): String {

        val handler = handlers[simpleName] ?: throw MissingHandler(
            "no handler registered for `$simpleName`\n" +
                    "    " + registeredHandlerString()
        )

        handler.apply {
            val r = requestSerializer.deserializer(payload)
            val res = handler(r, context)
            return responseSerializer.serializer(res)
        }
    }

    fun registeredHandlerString() =
        "registered handlers are: ${handlers.map { "\n   " + it.key }.joinToString("")}"

}

suspend inline fun <reified Req : Request<Resp>, reified Resp : Any> Req.sendRequest(
    dispatcher: suspend (String, String) -> String,
): Resp {
    val requestJson = Json.encodeToString(this)
    val responseJson = dispatcher(nameOf(Req::class), requestJson)
    val response = Json.decodeFromString<Resp>(responseJson)
    return response
}

interface Request<Resp : Any>

@Serializable
object VoidResponse