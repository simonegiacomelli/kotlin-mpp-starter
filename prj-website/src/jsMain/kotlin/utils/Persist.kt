package utils

import kotlinx.browser.localStorage
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Storage
import org.w3c.dom.get
import org.w3c.dom.set

class Persist(private val key: String, private val storage: Storage = localStorage) {
    private class ElementSerializer(val serializer: () -> String, val deserializer: (String) -> Unit)

    private val serializer = mutableMapOf<String, ElementSerializer>()
    fun add(input: HTMLInputElement, name: String): Persist {
        // could be cleaner,
        @Serializable
        data class Input(val value: String, val checked: Boolean)
        serializer[name] = ElementSerializer({
            Json.encodeToString(Input(input.value, input.checked))
        }, {
            runCatching {
                val i = Json.decodeFromString<Input>(it)
                input.value = i.value
                input.checked = i.checked
            }
        })
        return this
    }

    fun load() {
        val map = storage[key]?.let {
            runCatching { Json.decodeFromString<Map<String, String>>(it) }.getOrNull()
        } ?: emptyMap()

        serializer.forEach {
            val value = map[it.key]
            if (value != null) it.value.deserializer(value)
        }
    }

    fun save() {
        val map = serializer.map { it.key to it.value.serializer() }.toMap()
        storage[key] = Json.encodeToString(map)
    }
}

private fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is Number -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is Map<*, *> -> this.toJsonObject()
    is Iterable<*> -> JsonArray(this.map { it.toJsonElement() })
    is Array<*> -> JsonArray(this.map { it.toJsonElement() })
    else -> {
        println("unsupported type")
        JsonPrimitive(this.toString())
    } // Or throw some "unsupported" exception?
}

private fun Map<*, *>.toJsonObject(): JsonObject = JsonObject(map {
    it.key.toString() to it.value.toJsonElement()
}.toMap())

