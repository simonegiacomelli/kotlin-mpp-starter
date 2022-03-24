package rpc.oneway.topics

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import rpc.OnewayContextHandlers
import rpc.OnewwayContextHandler
import rpc.oneway.*

class BrowserTopic<Context>(
    val transport: Transport,
) {
    val handlers = OnewayContextHandlers<Context>()
    val subscriptions = mutableSetOf<TopicSubscription<Context>>()

    fun register(contextOnewayHandler: OnewayContextHandlers<Context>) {
        contextOnewayHandler.register { req: OnewayApiTopicPublish, context ->
            handlers.dispatchOneway(req.topicName, req.payLoad, context)
        }
    }

    inline fun <reified T> subscribe(noinline function: (message: T) -> Unit): TopicSubscription<Context> {
        val topicName = name<T>()
        val handler = handlers.register { message: T, _ -> function(message) }
        val subscription = TopicSubscription(topicName, handler)
        transport.send(OnewayApiTopicSubscribe(topicName))
        subscriptions.add(subscription)
        return subscription
    }

    /**
     * se il socket si disconnette e si riconnette, il server ha pulito tutte le subscription e vanno rimandate
     */
    fun onWebsocketReconnect() {
        transport.send(OnewayApiTopicResetAndSubscribe(subscriptions.map { it.topicName }))
    }

    inline fun <reified T> name() = T::class.simpleName ?: error("no name for reified T !?")

    fun unsubscribe(subscription: TopicSubscription<Context>) {
        transport.send(OnewayApiTopicUnsubscribe(subscription.topicName))
        handlers.unregister(subscription.handler)
    }

    inline fun <reified T> publish(instance: T) {
        transport.send(OnewayApiTopicPublish(name<T>(), Json.encodeToString(instance)))
    }

}

data class TopicSubscription<Context>(val topicName: String, val handler: OnewwayContextHandler<Context>)