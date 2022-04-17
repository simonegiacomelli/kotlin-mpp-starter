package accesscontrol

interface UserAbs {
    val id: Int
    val username: String
    val email: String?
    val roles: Set<Int>
}

