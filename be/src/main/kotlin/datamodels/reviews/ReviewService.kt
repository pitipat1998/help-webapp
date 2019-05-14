package datamodels.reviews

import datamodels.restaurants.Restaurant
import datamodels.restaurants.Restaurants
import datamodels.users.User
import datamodels.users.Users
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class ReviewService (val dbConn: Connection){
    init{
        createTable()
    }

    private fun createTable(){
        transaction {
            SchemaUtils.create(Reviews)
        }
    }

    private fun addAVG(prev: Double, x: Int, n: Int) : Double{
        return ((prev*n) + x)/ (n+1)
    }

    private fun delAVG(prev: Double, x:Int, n: Int) : Double{
        if  (n == 1) return 0.0
        return ((prev*n) - x)/ (n-1)
    }

    private fun updateAVG(avg: Double, prev: Int, next: Int, n: Int) : Double{
        val tempAVG = delAVG(avg, prev, n)
        return addAVG(tempAVG, next, n-1)
    }

    fun alreadyReview(user: User, restaurant: Restaurant) : Boolean {
         return transaction {
            val review = Review.find{
                (Reviews.userID eq user.id) and (Reviews.restaurantID eq restaurant.id)
            }.toList()
             if (review.size > 0) return@transaction true
             return@transaction false
        }
    }

    fun createReview(user: User, restaurant: Restaurant, ur: Int) : Int {
        if (alreadyReview(user, restaurant)) return -1
        return transaction {
            val review = Review.new {
                userID = user
                restaurantID = restaurant
                rating = ur
            }
            restaurant.globalRating = addAVG(restaurant.globalRating, review.rating, restaurant.ratingCount)
            restaurant.ratingCount += 1
            return@transaction review.id.value
        }
    }

    fun deleteReview(reviewID: Int, userID: Int) : Int{
        return transaction {
            val review = Review.findById(reviewID)
            if (review == null){
                return@transaction -1
            }
            else{
                if (review.userID.id.value != userID)
                    return@transaction -1
                else{
                    val restaurant = review.restaurantID
                    val id = review.id.value
                    restaurant.globalRating = delAVG(restaurant.globalRating, review.rating, restaurant.ratingCount)
                    restaurant.ratingCount -= 1
                    review.delete()
                    return@transaction id
                }
            }
        }
    }

    fun updateReview(reviewID: Int, userID: Int, rating: Int) : Int{
        return transaction {
            val review = Review.findById(reviewID)
            if (review == null){
                return@transaction -1
            }
            else{
                if (review.userID.id.value != userID)
                    return@transaction -1
                else{
                    val restaurant = review.restaurantID
                    restaurant.globalRating = updateAVG(restaurant.globalRating, review.rating, rating, restaurant.ratingCount)
                    review.rating = rating
                    return@transaction review.id.value
                }
            }
        }
    }

    fun restaurantReviews(id: Int) : List<ReviewResponse> {
        return transaction {
            return@transaction Review.find {
                Reviews.restaurantID eq id
            }.map { review ->
                ReviewResponse(
                        review.id.value,
                        review.restaurantID.id.value,
                        review.userID.id.value,
                        review.userID.username,
                        review.rating)
            }
        }
    }

    fun userReviews(id: Int) : List<ReviewResponse> {
        return transaction {
            Review.find {
                Reviews.userID eq id
            }.map {review ->
                ReviewResponse(
                        review.id.value,
                        review.restaurantID.id.value,
                        review.userID.id.value,
                        review.userID.username,
                        review.rating)
            }
        }
    }

}