package rpc.oneway

import kotlinx.serialization.Serializable


@Serializable
data class OnewayApiTopicSubscribe(val topicName: String)


@Serializable
data class OnewayApiTopicResetAndSubscribe(val topicNameList: List<String>)

@Serializable
data class OnewayApiTopicUnsubscribe(val topicName: String)


@Serializable
data class OnewayApiTopicPublish(val topicName: String, val payLoad: String)


@Serializable
class TopicGreeting(val greeting: String)

