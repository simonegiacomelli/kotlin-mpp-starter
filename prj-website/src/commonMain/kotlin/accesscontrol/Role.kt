package accesscontrol

import api.names.ApiAcPasswordChangeRequest
import api.names.ApiAddRequest
import kotlin.reflect.KClass

enum class Role(
    override val id: Int,
    override val apiWhitelist: Set<KClass<*>> = emptySet(),
    override val composedBy: Set<Role> = emptySet()
) : RoleAbs {
    Admin(AdminAbs.id),
    ChangePassword(2, setOf(ApiAcPasswordChangeRequest::class)),
    Calculator(3, setOf(ApiAddRequest::class))
}
