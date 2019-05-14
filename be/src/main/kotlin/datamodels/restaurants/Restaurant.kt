package datamodels.restaurants

import datamodels.cuisines.Cuisine
import datamodels.users.User
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class Restaurant(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<Restaurant>(Restaurants)
    var name by Restaurants.name
    var globalRating by Restaurants.globalRating
    var ratingCount by Restaurants.ratingCount
    var cuisineID  by Cuisine referencedOn Restaurants.cuisineID
    var createdBy by User referencedOn Restaurants.createBy
}