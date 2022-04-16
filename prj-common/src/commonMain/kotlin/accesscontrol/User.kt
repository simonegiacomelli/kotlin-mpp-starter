package accesscontrol

import kotlinx.serialization.Serializable

@Serializable
data class User(
    override val id: Long,
    override val username: String,
    override val email: String?,
) : UserAbs

fun UserAbs.toDataclass() = User(id, username, email)