package context

@kotlinx.serialization.Serializable
data class UserDc(
    override val id: Long,
    override val username: String,
    override val email: String?,
) : User

interface User {
    val id: Long
    val username: String
    val email: String?
}

fun User.toDataclass() = UserDc(id, username, email)

object Anonymous : User {
    private const val message = "Cannot access property. Anonymous user"
    override val id: Long get() = error(message)
    override val username: String get() = error(message)
    override val email: String get() = error(message)
}