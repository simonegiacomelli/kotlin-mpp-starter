package api.names

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import rpc.Request
import kotlin.reflect.KMutableProperty1

@Serializable
class ApiAcUserListRequest : Request<ApiAcUserListResponse>

@Serializable
data class AcUser(
    var id: Int = -1,
    var username: String = "",
    var email: String? = null,
    var phone_number: String = "",
    var lockout_end_date_utc: LocalDateTime? = null,
    var lockout_enabled: Boolean = true,
    var access_failed_count: Int = 0
) {
    companion object {
        fun properties(): List<KMutableProperty1<AcUser, *>> = listOf(
            AcUser::id,
            AcUser::username,
            AcUser::email,
            AcUser::phone_number,
            AcUser::lockout_end_date_utc,
            AcUser::lockout_enabled,
            AcUser::access_failed_count,
        )
    }

}

@Serializable
class ApiAcUserListResponse(val users: List<AcUser>)
