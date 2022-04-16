package context

import rpc.RpcMessage

fun authorizeDispatch(userRoles: Set<Int>, roles: Map<String, Set<Int>>, message: RpcMessage): Boolean {
    TODO()
}