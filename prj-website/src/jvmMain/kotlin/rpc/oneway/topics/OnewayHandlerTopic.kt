package rpc.oneway.topics

import folders.Folders
import kotlinx.datetime.Clock
import rpc.OnewayContextHandlers
import rpc.oneway.*


private typealias SubscriptionMap = MutableMap<String, MutableSet<WsEndpoint>>

fun setupTopicInfrastructure(contextOnewayHandlerJvm: OnewayContextHandlers<OnewayContext>, folders: Folders) {
    val L = Logger()
    val lock = Any()
    val subscriptions: SubscriptionMap = mutableMapOf()
    val allEndpoints = mutableSetOf<WsEndpoint>()
    fun pruneTopicsWithNoSubscriber() = subscriptions.entries.removeAll { it.value.isEmpty() }
    fun endpointsFor(topicName: String) = subscriptions.getOrPut(topicName) { mutableSetOf() }

    fun <T> locked(function: SubscriptionMap.() -> T): T {
        synchronized(lock) {
            return function(subscriptions)
        }
    }

    fun changed() {
        val proc = folders.data.resolve("proc")
        proc.mkdirs()
        proc.resolve("topic-subscriptions.txt").writeText(
            "Composed ${Clock.System.now()} \n\n" +
                    subscriptions.map {
                        "Topic: ${it.key}\n" + it.value.joinToString("\n") { "   " + it.strRepr() }
                    }.joinToString("\n\n")
        )
    }

    changed() // cosi' pulisce il file di stato

    wsEndpointPool.listeners.add { change ->
        locked {
            when (change) {
                is Add -> allEndpoints.add(change.wsEndpoint)
                is Remove -> {
                    values.forEach { it.remove(change.wsEndpoint) }
                    pruneTopicsWithNoSubscriber()
                }
            }
            changed()
        }
    }
    contextOnewayHandlerJvm.register { req: OnewayApiTopicSubscribe, context ->
        L.request(req)
        check(context.wsEndpoint is WsEndpointAnswerable) { "type is: `${context.wsEndpoint.javaClass.canonicalName}`" }
        locked {
            if (allEndpoints.contains(context.wsEndpoint))
                endpointsFor(req.topicName).add(context.wsEndpoint)
            changed()
        }
    }

    contextOnewayHandlerJvm.register { req: OnewayApiTopicUnsubscribe, context ->
        L.request(req)
        locked {
            endpointsFor(req.topicName).remove(context.wsEndpoint)
            pruneTopicsWithNoSubscriber()
            changed()
        }
    }

    contextOnewayHandlerJvm.register { req: OnewayApiTopicPublish, context ->
        L.request(req)
        val remotes = locked { endpointsFor(req.topicName) }
        remotes.filterIsInstance<WsEndpointAnswerable>().forEach { it.onewayApiSend(req) }
    }

    contextOnewayHandlerJvm.register { req: OnewayApiTopicResetAndSubscribe, context ->
        L.request(req)
        locked {
            if (allEndpoints.contains(context.wsEndpoint))
                req.topicNameList.forEach { endpointsFor(it).add(context.wsEndpoint) }
            changed()
        }
    }

    L.i("registration done")
}

private class Logger {
    fun i(string: String) = println(string)
}

private inline fun <reified T : Any> Logger.request(req: T) {
//    val simpleName = req::class.simpleName
    println("$req")
}
