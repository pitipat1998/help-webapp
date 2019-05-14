package datamodels.users

import org.jetbrains.exposed.dao.IntIdTable

object Users : IntIdTable() {
    val username = varchar( "username",100).uniqueIndex()
    val password = varchar("password", 60)
    val email = varchar("email", 320)
    val address = text("address")
    val lastLogin = datetime("last_login")
    val createAt = datetime("create_at")
}
