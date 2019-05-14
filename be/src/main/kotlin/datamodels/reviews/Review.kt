package datamodels.reviews

import datamodels.restaurants.Restaurant
import datamodels.users.User
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class Review(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<Review>(Reviews)
    var userID  by User referencedOn Reviews.userID
    var restaurantID by Restaurant referencedOn Reviews.restaurantID
    var rating by Reviews.rating
}