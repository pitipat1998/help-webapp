package datamodels.reviews

import datamodels.restaurants.Restaurants
import datamodels.users.Users
import org.jetbrains.exposed.dao.IntIdTable

object Reviews : IntIdTable() {
    val userID = reference( "user_id", Users)
    val restaurantID = reference("restaurant_id", Restaurants)
    val rating = integer("rating")
}

