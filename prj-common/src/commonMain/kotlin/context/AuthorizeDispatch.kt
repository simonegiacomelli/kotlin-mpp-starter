package context

import accesscontrol.Anonymous
import accesscontrol.UserAbs
import rpc.RpcMessage

fun authorizeDispatch(message: RpcMessage, user: UserAbs, roles: Map<String, Set<Int>>): Int {
    val requiredRoles = roles.getOrElse(message.name) { emptySet() }
    if (requiredRoles.isEmpty()) return 200
    if (user is Anonymous) return 401 // see https://stackoverflow.com/a/14713094/316766
    val intersection = user.roles.intersect(requiredRoles)
    val forbidden = intersection.isEmpty()
    return if (forbidden) 403 else 200
}