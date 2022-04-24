package api.names

import kotlinx.serialization.Serializable
import rpc.Request
import rpc.VoidResponse

@Serializable
class ApiTmNewEventRequest(val type_id: Int, val arguments: String = "") : Request<VoidResponse>
