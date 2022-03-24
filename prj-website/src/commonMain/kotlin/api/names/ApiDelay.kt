package api.names

import kotlinx.serialization.Serializable
import rpc.Request

@Serializable
class ApiDelayRequest(val delayMilliseconds: Long) : Request<ApiDelayResponse>

@Serializable
class ApiDelayResponse()
