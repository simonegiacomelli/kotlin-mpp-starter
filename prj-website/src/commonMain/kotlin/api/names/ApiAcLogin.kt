package api.names

import kotlinx.serialization.Serializable
import rpc.Request

@Serializable
class ApiAcLoginRequest(val username: String, val password: String) : Request<ApiAcLoginResponse>

@Serializable
class ApiAcLoginResponse()
