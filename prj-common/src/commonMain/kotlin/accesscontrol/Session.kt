package accesscontrol

import kotlinx.serialization.Serializable

@Serializable
class Session(val id: String, val user: User)