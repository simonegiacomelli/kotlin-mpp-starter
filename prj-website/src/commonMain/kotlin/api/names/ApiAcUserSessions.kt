package api.names

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import rpc.Request

@Serializable
class ApiAcUserSessionsRequest : Request<ApiAcUserSessionsResponse>

private val epoch = LocalDateTime(1970, 1, 1, 0, 0)

@Serializable
data class AcSession(
    var id: String = "",
    var user_id: Int = -1,
    var username: String = "",
    var created_at: LocalDateTime = epoch,
    var updated_at: LocalDateTime = epoch,
) {
    companion object {
        fun properties() = listOf(
            AcSession::id,
            AcSession::user_id,
            AcSession::username,
            AcSession::created_at,
            AcSession::updated_at,
        )
    }

}

@Serializable
class ApiAcUserSessionsResponse(val sessions: List<AcSession>)
