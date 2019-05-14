package datamodels.restaurants

class RestaurantResponse(val id: Int?, val name : String?, val globalRating : Double?,
                         val ratingCount : Int?, val cuisine: String?, val createdBy: String?)

class PartyRestaurantResponse(val id: Int?, val name : String?,
                         val partyRating : Double?)
