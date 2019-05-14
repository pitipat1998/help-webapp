package datamodels.statistics

import datamodels.users.StatisticsUserResponse

class StatisticsResponse(
                         val totRestaurant: Int,
                         val totUsers: Int,
                         val totRatings: Int,
                         val avgRatingPerRestaurant: Double,
                         val avgRatingPerUser: Double,
                         val totParties: Int,
                         val avgUserPerParty: Double,
                         val topMembers: List<StatisticsUserResponse>
                        )