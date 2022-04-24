package jdbcx

import kotlinx.serialization.Serializable

interface ColumnBase {
    val name: String
    val nullable: Boolean
}

fun List<ColumnBase>.existsColumn(name: String): Boolean = any { it.name.equals(name, ignoreCase = true) }


@Serializable
data class ColumnBaseDc(
    /** Il nome e' sempre in lowercase */
    override val name: String, override val nullable: Boolean
) : ColumnBase
