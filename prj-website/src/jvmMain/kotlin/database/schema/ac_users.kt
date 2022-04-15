package database.schema

object ac_users : LongIdTable(), CreatedAt {
    val email = varchar("email", 256).nullable()
    val email_confirmed = bool("email_confirmed")
    val password_hash = text("password_hash").nullable()
    val security_stamp = text("security_stamp").nullable()
    val phone_number = text("phone_number").nullable()
    val phone_number_confirmed = bool("phone_number_confirmed")
    val two_factor_enabled = bool("two_factor_enabled")
    val lockout_end_date_utc = datetime("lockout_end_date_utc").nullable()
    val lockout_enabled = bool("lockout_enabled")
    val access_failed_count = integer("access_failed_count")
    val username = varchar("username", 256).uniqueIndex("ac_users_idx_username")
    override val created_at = createdAt()
}

/*
Set the HttpOnly attribute using the Set-Cookie HTTP header to bar client-side scripts from accessing cookies. This measure will also protect your web site/application from cross-site scripting (XSS) and other JavaScript injection attacks. Adding the Secure and SameSite directives is also recommended.

Regenerate the session ID after the user has been authenticated. This will close the door on session fixation attacks because the session ID changes before the attacker has a chance to use it. Also, consider changing the session ID with every user request. This would significantly reduce the amount of time an attacker would have to exploit a compromised session ID.

open class BbxToken : TypedRow() {
    var id: String        by notNullVar
    val token: String
        get() = id
    var idSocieta: String?        by nullVar
    var idCollaboratore: Int?     by nullVar
    var idUtente: Int?            by nullVar
    var dsData: String?           by nullVar
}

*/
