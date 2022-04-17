package rpc

data class RpcMessage(val name: String, val payload: String) {

    companion object {
        private const val apiMarker = "#kotlin"
        fun isRecognizedMessage(payload: String): Boolean {
            return payload.startsWith(apiMarker)
        }

        fun decode(message: String): RpcMessage {
            val requestStr = message.removePrefix(apiMarker)
            val (header, payload) = requestStr.split("\n\n", limit = 2)
            val props = header.split("\n").filterNot { it.trim().isEmpty() }.associate {
                val kv = it.split("=")
                kv[0] to kv[1]
            }
            val simpleName = props["simpleName"] ?: error("No simpleName specified for message `$message`")
            return RpcMessage(simpleName, payload)
        }

        fun encode(simpleName: String, payload: String): String {
            val message = ("$apiMarker\n" +
                    "simpleName=$simpleName\n" +
                    "\n"
                    + payload)
            return message
        }
    }
}