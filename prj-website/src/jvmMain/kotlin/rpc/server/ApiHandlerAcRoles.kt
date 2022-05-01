package rpc.server

import accesscontrol.Role
import api.names.AcRole
import api.names.ApiAcRolesRequest
import api.names.ApiAcRolesResponse
import database.databinding.ReifiedMapper
import database.databinding.exposedMapper
import database.schema.ac_roles
import database.time.nowAtDefault
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

private val reg1 = contextHandler.register { req: ApiAcRolesRequest, _ ->
    val mapper = exposedMapper { AcRole() }.bindTo(ac_roles)
    synchronizeRoles(mapper)
    ApiAcRolesResponse(transaction { acRoles(mapper) })
}

private fun acRoles(mapper: ReifiedMapper<AcRole>) = ac_roles.slice(mapper.columns).selectAll().map { mapper.map(it) }

private fun synchronizeRoles(mapper: ReifiedMapper<AcRole>) {
    transaction { ac_roles.deleteWhere { ac_roles.id.notInList(Role.values().map { it.id }) } }
    transaction {
        val nowAtDefault = nowAtDefault()
        val dbRoles = acRoles(mapper).associateBy { it.id }
        Role.values().forEach { role ->
            val dbRole = dbRoles[role.id]

            if (dbRole != null) {
                if (role.name != dbRole.name)
                    ac_roles.update({ ac_roles.id.eq(role.id) }) {
                        it[name] = role.name
                        it[updated_at] = nowAtDefault
                    }
            } else {
                ac_roles.insert {
                    it[id] = role.id
                    it[name] = role.name
                    it[created_at] = nowAtDefault
                    it[updated_at] = nowAtDefault
                }
            }
        }
    }
}
