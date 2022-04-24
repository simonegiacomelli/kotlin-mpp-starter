package grid

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Settings(val hidden: Set<String> = emptySet(), val order: List<String> = emptyList())

fun Settings.Companion.fromJson(json: String?): Settings {
    return try {
        val j = json ?: "{}"
        Json.decodeFromString(j)
    } catch (ex: Exception) {
        Settings()
    }
}

fun Settings.toJson(): String = Json.encodeToString(this)