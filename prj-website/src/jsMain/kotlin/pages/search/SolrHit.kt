package pages.search

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class SolrHit(val id: String, val description: String, val stargazer: Int, val url: String? = null) {
    companion object {
        operator fun invoke(string: String) = json.decodeFromString<SolrHit>(string)
    }
}

private val json = Json { ignoreUnknownKeys = true }
