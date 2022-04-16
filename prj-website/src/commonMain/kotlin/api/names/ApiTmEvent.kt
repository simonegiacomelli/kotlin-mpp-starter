package api.names

import kotlinx.serialization.Serializable
import rpc.Request
import rpc.VoidResponse

@Serializable
class ApiTmEventRequest(val type_id: Int, val arguments: String = "") : Request<VoidResponse>
