package datamodels.restaurants

import datamodels.cuisines.Cuisine
import datamodels.reviews.Review
import datamodels.reviews.Reviews
import datamodels.users.User
import java.sql.Connection
import java.util.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class RestaurantService(val dbConn : Connection){
    init {
        createTable()
//        init()
    }

    private fun createTable(){
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Restaurants)
        }
    }

    fun createRestaurant(rn: String, cid: Int, user: User) : Int{
        return transaction {
            val id = Restaurant.new {
                name = rn
                globalRating = 0.0
                ratingCount = 0
                cuisineID = Cuisine.findById(cid)!!
                createdBy = user
            }.id.value
            return@transaction id
        }
    }

    fun retrieveRestaurant(id : Int) : RestaurantResponse? {
        val sql = """
            select distinct Restaurants.id ,Restaurants.name, Restaurants.global_rating, Restaurants.rating_count, C.cuisine, U.username
            from Restaurants
              inner join Users U on Restaurants.create_by = U.id
              inner join Cuisines C on Restaurants.cuisine_id = C.id
            where Restaurants.id = ?;
        """.trimIndent()
        val prepStmt = dbConn.prepareStatement(sql)
        with(prepStmt){
            setInt(1, id)
        }
        val rs = prepStmt.executeQuery()
        val results = ArrayList<RestaurantResponse>()
        while (rs.next()) {
            results.add(RestaurantResponse(
                    id = rs.getInt("id"),
                    name = rs.getString("name"),
                    globalRating = rs.getDouble("global_rating"),
                    ratingCount = rs.getInt("rating_count"),
                    cuisine = rs.getString("cuisine"),
                    createdBy = rs.getString("username")
            ))
        }
        if(results.size > 0)
            return results[0]
        return null
    }

    fun deleteRestaurant(restaurantID: Int, userID: Int) : Int{
        return transaction {
            val restaurant = Restaurant.findById(restaurantID)
            if (restaurant == null){
                return@transaction -1
            }
            else{
                if (restaurant.createdBy.id.value != userID)
                    return@transaction -1
                else{
                    val id = restaurant.id.value
                    Review.find{
                        Reviews.restaurantID eq restaurantID
                    }.forEach{review -> review.delete()}
                    restaurant.delete()
                    return@transaction id
                }
            }
        }
    }

    fun userRatedRestaurant(id: Int) : List<RestaurantResponse>{
        val sql = """
            select distinct Restaurants.id restaurant_id ,Restaurants.name, Restaurants.global_rating, Restaurants.rating_count, C.cuisine, U.username
            from Restaurants
              inner join Users U on Restaurants.create_by = U.id
              inner join Cuisines C on Restaurants.cuisine_id = C.id
              inner join Reviews R2 on Restaurants.id = R2.restaurant_id
            where R2.user_id = ?
            order by Restaurants.name;
        """.trimIndent()
        val prepStmt = dbConn.prepareStatement(sql)
        with(prepStmt){
            setInt(1, id)
        }
        val rs = prepStmt.executeQuery()
        val results = ArrayList<RestaurantResponse>()
        while (rs.next()){
            results.add(RestaurantResponse(
                    id = rs.getInt("restaurant_id"),
                    name = rs.getString("name"),
                    globalRating = rs.getDouble("global_rating"),
                    ratingCount = rs.getInt("rating_count"),
                    cuisine = rs.getString("cuisine"),
                    createdBy = rs.getString("username")
            ))
        }
        return results
    }
    fun userUnratedRestaurant(id:Int) : List<RestaurantResponse>{
        val sql = """
            with user_rated as (
            select distinct restaurant_id
            from Reviews
              inner join Users U on  Reviews.user_id = U.id
              inner join Restaurants R on R.id = Reviews.restaurant_id
            where U.id = ?
            ),
            all_res as (
            select distinct U.id user_id, Restaurants.id restaurant_id ,Restaurants.name, Restaurants.global_rating, Restaurants.rating_count, C.cuisine, U.username
            from Restaurants
              inner join Users U on Restaurants.create_by = U.id
              inner join Cuisines C on Restaurants.cuisine_id = C.id
            )
            select * from all_res where all_res.restaurant_id not in (select * from user_rated)
            order by all_res.name;
        """.trimIndent()
        val prepStmt = dbConn.prepareStatement(sql)
        with(prepStmt){
            setInt(1, id)
        }
        val rs = prepStmt.executeQuery()
        val results = ArrayList<RestaurantResponse>()
        while (rs.next()){
            results.add(RestaurantResponse(
                    id = rs.getInt("restaurant_id"),
                    name = rs.getString("name"),
                    globalRating = rs.getDouble("global_rating"),
                    ratingCount = rs.getInt("rating_count"),
                    cuisine = rs.getString("cuisine"),
                    createdBy = rs.getString("username")
            ))
        }
        return results
    }
}