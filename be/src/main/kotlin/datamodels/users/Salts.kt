package datamodels.users

import org.jetbrains.exposed.dao.IntIdTable

object Salts : IntIdTable() {
    val userIn = varchar("user_in", 64).uniqueIndex()
    val salt = varchar("salt", 29)
}