package datamodels.cuisines

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class Cuisine(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<Cuisine>(Cuisines)
    var cuisine by Cuisines.cuisine
}