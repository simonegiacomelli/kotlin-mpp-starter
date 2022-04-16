package menu

import accesscontrol.Role.ChangePassword

class AccessControl(parent: Menu) : Menu(parent, "access_control", "Access Control") {
    val userCreate = menu("user_create", "Create user")
    val userPasswd = menu("user_passwd", "Set user password")
    val userChange = menu("user_change", "Change password", ChangePassword)
}

