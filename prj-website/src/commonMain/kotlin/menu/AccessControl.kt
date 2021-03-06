package menu

import accesscontrol.Role.Admin
import accesscontrol.Role.ChangePassword

class AccessControl(parent: Menu) : Menu(parent, "access_control", "Access Control") {
    val userList = menu("user_list", "Users list", Admin)
    val roleList = menu("role_list", "Role list", Admin)
    val userSessions = menu("user_sessions", "Users sessions", Admin)
    val userCreate = menu("user_create", "Create user", Admin)
    val userPasswd = menu("user_passwd", "Set user password", Admin)
    val userChange = menu("user_change", "Change password", ChangePassword)
}

