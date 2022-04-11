package api.names

import kotlinx.serialization.Serializable
import rpc.Request

@Serializable
class ApiTmEventRequest(val type_id: Int, val arguments: String = "") : Request<ApiTmEventResponse>

@Serializable
class ApiTmEventResponse()
