package api.names

import kotlinx.serialization.Serializable
import rpc.Request

@Serializable
class ApiAddRequest(val a: Int, val b: Int) : Request<ApiAddResponse>

@Serializable
class ApiAddResponse(val result: Int)
