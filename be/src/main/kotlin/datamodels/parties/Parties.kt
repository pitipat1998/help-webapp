package datamodels.parties

import org.jetbrains.exposed.dao.IntIdTable

object Parties : IntIdTable() {
    val name = varchar( "name",100)
    val noMembers = integer("no_members")
}

