package api.names

import kotlinx.serialization.Serializable
import rpc.Request

@Serializable
class ApiTmEventRequest(val a: Int, val b: Int) : Request<ApiTmEventResponse>

@Serializable
class ApiTmEventResponse(val result: Int)
