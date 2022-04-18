package accesscontrol

import accesscontrol.Access.*


enum class Access(val status: Int) { // see https://stackoverflow.com/a/14713094/316766
    Allowed(200),

    /** Not authenticated, aka Unauthorized (even if it's kind of misleading) */
    NoAuthentication(401),

    /** Authenticated bud no permission, aka Forbidden */
    NoPermission(403)
}

fun UserAbs.access(requiredRoles: Set<Int>) = when {
    requiredRoles.isEmpty() -> Allowed
    this is Anonymous -> NoAuthentication
    else -> if (allowed(roles, requiredRoles)) Allowed else NoPermission
}

fun Set<Int>.allows(userRoles: Set<Int>) = allowed(userRoles, this)
fun Set<Int>.allowedBy(requiredRoles: Set<Int>) = allowed(this, requiredRoles)

fun allowed(userRoles: Set<Int>, requiredRoles: Set<Int>) =
    requiredRoles.isEmpty() || userRoles.contains(AdminAbs.id) || requiredRoles.intersect(userRoles).isNotEmpty()