package api.names

import kotlinx.serialization.Serializable
import rpc.Request
import rpc.VoidResponse


@Serializable
class ApiAcUserCreateRequest(val username: String, val password: String) : Request<VoidResponse>
