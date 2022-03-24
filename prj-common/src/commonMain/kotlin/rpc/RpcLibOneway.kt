package rpc

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


inline fun <reified Req> sendOneway(
    request: Req,
    dispatcher: (String, String) -> Unit,
) {
    val requestJson = Json.encodeToString(request)
    dispatcher(Req::class.simpleName ?: error("no class name"), requestJson)
}

data class OnewwayContextHandler<Context>(
    val name: String,
    val requestSerializer: AnySerializer,
    val handler: (Any, Context) -> Unit,
)

class OnewayContextHandlers<Context> {


    val handlers = mutableMapOf<String, OnewwayContextHandler<Context>>()

    inline fun <reified Req> register(
        noinline function: (Req, Context) -> Unit,
    ): OnewwayContextHandler<Context> {
        val handlerName = Req::class.simpleName ?: error("no name")
        if (handlers.containsKey(handlerName)) error("Handler gia' registrato `$handlerName`")

        val onewwayContextHandler = OnewwayContextHandler(
            name = handlerName,
            requestSerializer = serializers<Req>(),
            handler = function as (Any, Context) -> Unit
        )
        handlers[handlerName] = onewwayContextHandler
        return onewwayContextHandler
    }

    fun unregister(context: OnewwayContextHandler<Context>) {
        if (!handlers.containsKey(context.name)) error("Handler non registrato `${context.name}`")
        handlers.remove(context.name)
    }

    fun dispatchOneway(m: RpcMessage, context: Context) = dispatchOneway(m.simpleName, m.payload, context)
    fun dispatchOneway(payload: String, context: Context) =
        dispatchOneway(RpcMessage.decode(payload), context)

    fun dispatchOneway(simpleName: String, payload: String, context: Context) {

        val handler = handlers[simpleName] ?: throw MissingHandler("no handler registered for `$simpleName`")

        handler.apply {
            val r = requestSerializer.deserializer(payload)
            handler(r, context)
        }
    }

}