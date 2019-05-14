package datamodels.parties

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class Party(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<Party>(Parties)
    var name by Parties.name
    var noMembers by Parties.noMembers
}
