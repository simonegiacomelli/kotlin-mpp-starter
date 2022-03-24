package api.names

import kotlinx.serialization.Serializable
import rpc.Request

@Serializable
class ApiSearchRequest(val queryParams: Map<String, String>) : Request<ApiSearchResponse>

@Serializable
class ApiSearchResponse(val hits: List<String>, val response: String)

