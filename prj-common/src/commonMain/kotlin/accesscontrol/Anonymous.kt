package accesscontrol

object Anonymous : UserAbs {
    private const val message = "Cannot access property. Anonymous user"
    override val id: Int get() = error(message)
    override val username: String get() = error(message)
    override val email: String get() = error(message)
    override val roles: Set<RoleAbs> = error(message)
}