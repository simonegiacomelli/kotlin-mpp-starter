package api.names

import kotlinx.serialization.Serializable
import rpc.Request
import rpc.VoidResponse

@Serializable
class ApiDelayRequest(val delayMilliseconds: Long) : Request<VoidResponse>
