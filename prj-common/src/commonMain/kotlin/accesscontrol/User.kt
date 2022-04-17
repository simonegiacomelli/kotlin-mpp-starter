package accesscontrol

import kotlinx.serialization.Serializable

@Serializable
data class User(
    override val id: Int,
    override val username: String,
    override val email: String?,
    override val roles: Set<Int>,
) : UserAbs

fun UserAbs.toDataclass() = User(id, username, email, roles)