package datamodels.users

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class User(id : EntityID<Int>) : IntEntity(id){
    companion object : IntEntityClass<User>(Users)
    var username by Users.username
    var password by Users.password
    var email by Users.email
    var address by Users.address
    var lastLogin by Users.lastLogin
    var createAt by Users.createAt
}
