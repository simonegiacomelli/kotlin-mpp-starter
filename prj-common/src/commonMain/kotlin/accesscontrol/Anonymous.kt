package accesscontrol

object Anonymous : UserAbs {
    private const val message = "Anonymous user, all properties are mock"
    override val id: Int get() = error(message)
    override val username: String get() = error(message)
    override val email: String get() = error(message)
    override val roles: Set<RoleAbs> get() = error(message)
}