package datamodels.restaurants

import datamodels.cuisines.Cuisines
import datamodels.users.Users
import org.jetbrains.exposed.dao.IntIdTable

object Restaurants : IntIdTable() {
    val name = varchar( "name",100)
    val globalRating = double("global_rating")
    val ratingCount = integer("rating_count")
    val cuisineID = reference("cuisine_id", Cuisines)
    val createBy = reference("create_by", Users)
}

