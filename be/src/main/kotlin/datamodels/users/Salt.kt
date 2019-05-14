package datamodels.users

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class Salt(id : EntityID<Int>) : IntEntity(id){
    companion object : IntEntityClass<Salt>(Salts)
    var userIn by Salts.userIn
    var salt by Salts.salt
}

