package api.names

import context.UserDc
import kotlinx.serialization.Serializable
import rpc.Request

@Serializable
class Credential(val username: String, val password: String)

@Serializable
class ApiAcLoginRequest(val credential: Credential) : Request<ApiAcLoginResponse>

@Serializable
class ApiAcSession(val id: String, val user: UserDc)

@Serializable
class ApiAcLoginResponse(val session: ApiAcSession?)
