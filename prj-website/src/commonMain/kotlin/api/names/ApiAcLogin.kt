package api.names

import kotlinx.serialization.Serializable
import rpc.Request

@Serializable
class Credential(val username: String, val password: String)

@Serializable
class ApiAcLoginRequest(val credential: Credential) : Request<ApiAcLoginResponse>

@Serializable
class ApiAcLoginResponse(val session_id: String)
