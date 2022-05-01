package api.names

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import rpc.Request

@Serializable
class ApiAcRolesRequest : Request<ApiAcRolesResponse>

private val epoch = LocalDateTime(1970, 1, 1, 0, 0)

@Serializable
data class AcRole(
    var id: Int = -1,
    var name: String = "",
    var created_at: LocalDateTime = epoch,
    var updated_at: LocalDateTime = epoch,
) {
    companion object {
        fun properties() = listOf(
            AcRole::id,
            AcRole::name,
            AcRole::created_at,
            AcRole::updated_at,
        )
    }

}

@Serializable
class ApiAcRolesResponse(val roles: List<AcRole>)
