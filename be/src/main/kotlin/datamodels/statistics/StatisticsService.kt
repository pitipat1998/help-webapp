package datamodels.statistics

import datamodels.parties.Party
import datamodels.restaurants.Restaurant
import datamodels.reviews.Review
import datamodels.users.StatisticsUserResponse
import datamodels.users.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class StatisticsService(val dbConn : Connection){
    fun statistics() : StatisticsResponse{
        return transaction {
            val totRestaurants = Restaurant.all().count()
            val totUsers = User.all().count()
            val totRatings = Review.all().count()
            val avgRatingPerRestaurant = totRatings.toDouble()/totRestaurants.toDouble()
            val avgRatingPerUser = totRatings.toDouble()/totUsers.toDouble()
            val totParties = Party.all().count()
            val avgUserPerParty = totUsers.toDouble()/totParties.toDouble()
            val sql = """
                select username, tot_membership from
                (select user_id, count(user_id) tot_membership from Memberships group by user_id) party_members
                inner join Users on Users.id = party_members.user_id order by tot_membership desc, username asc
                limit 10
            """.trimIndent()
            val prepStmt = dbConn.prepareStatement(sql)
            val rs = prepStmt.executeQuery()
            val users = ArrayList<StatisticsUserResponse>()
            while(rs.next()){
                users.add(StatisticsUserResponse(
                        rs.getString("username"),
                        rs.getInt("tot_membership")
                    )
                )
            }

            return@transaction StatisticsResponse(
                   totRestaurants,
                    totUsers,
                    totRatings,
                    avgRatingPerRestaurant,
                    avgRatingPerUser,
                    totParties,
                    avgUserPerParty,
                    users
            )

        }
    }
}