package api.names

import kotlinx.serialization.Serializable
import rpc.Request

class UserCredential(val username: String, val password: String)

@Serializable
class ApiAcLoginRequest(val username: String, val password: String) : Request<ApiAcLoginResponse>

@Serializable
class ApiAcLoginResponse(val session_id: String)
