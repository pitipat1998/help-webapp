package web

import datamodels.DataService
import datamodels.SignInController
import datamodels.cuisines.CuisineResponse
import datamodels.general.Response
import datamodels.general.StatusCode
import datamodels.parties.Parties
import datamodels.parties.Party
import datamodels.parties.PartyResponse
import datamodels.restaurants.PartyRestaurantResponse
import datamodels.restaurants.Restaurant
import datamodels.restaurants.RestaurantResponse
import datamodels.reviews.ReviewResponse
import datamodels.statistics.StatisticsResponse
import datamodels.users.PartyUserResponse
import datamodels.users.User
import datamodels.users.UserResponse
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.core.util.Header
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun main(args: Array<String>) {
    val app = Javalin.create().enableCorsForAllOrigins().start(8555)
    app.before { ctx ->
        if (ctx.method() == "OPTIONS") {
            ctx.header(Header.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
        }
        ctx.header("Access-Control-Request-Headers", "content-type,access-control-allow-credentials")
    }

    val domain = "localhost"
    val dbName = "helpdb"
    val port = 3307
    val url = "jdbc:mysql://$domain:$port/"
    val connProps = Properties()
    with(connProps){
        put("user","root")
        put("password", "12345")
    }

    val DS : DataService = DataService(connectionString = url, dbName = dbName, connectionProperties = connProps)

        app.get("/api/authentication"){ctx ->
            val username = ctx.sessionAttribute<Int>("username")
            val user = DS.users.retrieveUser(username)
            val res : StatusCode
            if(user == null)
                res = StatusCode(false, "Not Authenticate", null)
            else
                res = StatusCode(true, "Authenticate", user.username)
            ctx.json(res)
        }

        app.post("/api/sign-in") {ctx ->
            val req : Map<String, Any> = ctx.body<Map<String, Any>>()
            val user : User? = DS.users.userLogin(
                req["username"].toString(),
                req["password"].toString()
            )
            val res : StatusCode
            if(user == null){
                res = StatusCode(false, "No Username Found", null)
            }
            else{
                res = StatusCode(true, "Username ${user.username} Login Successful", user.username)
                ctx.sessionAttribute("username", user.id.value)
            }
            ctx.json(res)
        }

    app.post("/api/sign-up"){ctx ->
        val req : Map<String, String> = ctx.body<Map<String, String>>()
        val user : User? = DS.users.userRegistration(
                req["username"].orEmpty(),
                req["password"].orEmpty(),
                req["email"].orEmpty(),
                req["address"].orEmpty()
        )
        val res : StatusCode
        if(user == null) {
            res = StatusCode(false, "Duplicate Username", null)
        }
        else{
            res = StatusCode(true, "Username ${user.username} Registration Successful", user.username)
            ctx.sessionAttribute("username", user.id.value)
        }
        ctx.json(res)
    }

    app.get("/api/sign-out"){ctx ->
        val username : Int? = ctx.sessionAttribute<Int>("username")
        var res : StatusCode
        if (username == null){
            res = StatusCode(false, "User is not logged in", null)
        }
        else{
            ctx.sessionAttribute("username", null)
            res = StatusCode(true, "Successfully Sign Out", null)
        }
        ctx.json(res)
    }

    app.get("/api/user-profile"){ctx ->
        val username : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(username)
        val res : Response
        if (user == null){
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        }
        else{
            val userInfo : UserResponse? = DS.users.userProfile(user.id.value)
            res = Response(userInfo, StatusCode(true, "Authenticated", user.username))
        }
        ctx.json(res)
    }

    app.get("/api/restaurants"){ctx ->
        val username : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(username)
        val res : Response
        if (user == null){
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        }
        else{
            val ratedRestaurants : List<RestaurantResponse> = DS.restaurants.userRatedRestaurant(user.id.value)
            val unratedRestaurants : List<RestaurantResponse> = DS.restaurants.userUnratedRestaurant(user.id.value)
            res = Response(
                    arrayListOf(ratedRestaurants, unratedRestaurants),
                    StatusCode(true, "Authenticated", user.username)
            )
        }
        ctx.json(res)
    }

    app.post("/api/restaurants"){ctx ->
        val uid : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(uid)
        val res : Response
        if(user == null)
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        else{
            val req : Map<String, Any> = ctx.body<Map<String, Any>>()
            val name : String = req["name"].toString()
            val cuisineID : Int = req["cuisineID"].toString().toInt()
            val id : Int = DS.restaurants.createRestaurant(name, cuisineID, user)
            res = Response(null, StatusCode(true, "restaurant $id has been created", user.username))
        }
        ctx.json(res)
    }


    app.get("/api/restaurants/:restaurant_id"){ctx ->
        val id : Int = ctx.pathParam("restaurant_id").toInt()
        val username : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(username)
        val res : Response
        if (user == null){
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        }
        else{
            val restaurantResponse : RestaurantResponse? = DS.restaurants.retrieveRestaurant(id)
            val restaurant : Restaurant? = transaction { Restaurant.findById(id) }
            if (restaurantResponse == null || restaurant == null)
                res = Response(arrayListOf(null, null, null), StatusCode(false, "no restaurant found", user.username))
            else{
                val reviews : List<ReviewResponse> = DS.reviews.restaurantReviews(id)
                val alreadyReview : Boolean = DS.reviews.alreadyReview(user, restaurant)
                res = Response(arrayListOf(restaurantResponse, reviews, alreadyReview), StatusCode(true, "restaurant $id found", user.username))
            }
        }
        ctx.json(res)
    }

    app.delete("/api/restaurants/:restaurant_id"){ctx ->
        val restaurantID : Int = ctx.pathParam("restaurant_id").toInt()
        val username : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(username)
        val res : Response
        if (user == null)
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        else{
            val deletedRestaurant : Int = DS.restaurants.deleteRestaurant(restaurantID, user.id.value)
            if (deletedRestaurant == -1)
               res = Response(deletedRestaurant, StatusCode(false, "restaurant not found", user.username))
            else
               res = Response(deletedRestaurant, StatusCode(true, "restaurant $restaurantID had been deleted", user.username))
        }
        ctx.json(res)
    }

    app.post("/api/reviews/:restaurant_id"){ctx ->
        val rid : Int= ctx.pathParam("restaurant_id").toInt()
        val req : Map<String, Any> = ctx.body<Map<String, Any>>()
        val rating : Int = req["rating"].toString().toInt()
        val username : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(username)
        val res : Response
        if (user == null){
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        }
        else{
            val restaurant : Restaurant? = transaction { return@transaction Restaurant.findById(rid)}
            if (restaurant == null)
                res = Response(-1, StatusCode(false, "No Restaurant", null))
            else{
                if( rating == null)
                    res = Response(-1, StatusCode(false, "No value", null))
                else {
                    val review : Int = DS.reviews.createReview(user, restaurant, rating.toString().toInt())
                    res = Response(review, StatusCode(true, "Review Created", user.username))
                }
            }
        }
        ctx.json(res)
    }

    app.delete("/api/reviews/:review_id"){ctx ->
        val reviewID : Int = ctx.pathParam("review_id").toInt()
        val username : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(username)
        val res : Response
        if (user == null)
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        else{
            val deletedReview : Int = DS.reviews.deleteReview(reviewID, user.id.value)
            if (deletedReview == -1)
               res = Response(deletedReview, StatusCode(false, "review not found", user.username))
            else
               res = Response(deletedReview, StatusCode(true, "review $reviewID had been deleted", user.username))
        }
        ctx.json(res)

    }

    app.patch("/api/reviews/:review_id"){ctx ->
        val reviewID : Int = ctx.pathParam("review_id").toInt()
        val username : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(username)
        val rating : Int? = ctx.body<Map<String, Any>>()["rating"].toString().toInt()
        val res : Response
        if (user == null || rating == null)
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        else{
            val updatedReview : Int = DS.reviews.updateReview(reviewID, user.id.value, rating)
            if (updatedReview == -1)
               res = Response(updatedReview, StatusCode(false, "review not found", user.username))
            else
               res = Response(updatedReview, StatusCode(true, "review $reviewID had been updated", user.username))
        }
        ctx.json(res)
    }

    app.get("/api/cuisines"){ctx ->
        val uid : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(uid)
        val res : Response
        if (user == null){
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        }
        else{
            val cuisines : List<CuisineResponse> = DS.cuisines.allCuisines()
            res = Response(cuisines, StatusCode(true, "Success", user.username))
        }
        ctx.json(res)
    }

    app.get("/api/parties"){ctx->
        val uid : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(uid)
        val res : Response
        if (user == null || uid == null){
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        }
        else{
            val userJoined : List<PartyResponse> = DS.parties.userJoinedParties(uid)
            val userNotJoined : List<PartyResponse> = DS.parties.userUnjoinedParties(uid)
            res = Response(arrayListOf(userJoined, userNotJoined), StatusCode(true, "Success", user.username))
        }
        ctx.json(res)
    }

    app.post("/api/parties"){ctx->
        val uid : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(uid)
        val res : Response
        if(user == null)
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        else{
            val req : Map<String, Any> = ctx.body<Map<String, Any>>()
            val name : String = req["name"].toString()
            val id : Int = DS.parties.createParty(user, name)
            res = Response(null, StatusCode(true, "party $id has been created", user.username))
        }
        ctx.json(res)
    }

    app.get("/api/parties/:party_id"){ctx ->
        val party_id : Int = ctx.pathParam("party_id").toInt()
        val username : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(username)
        val res : Response
        if (user == null ){
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        }
        else{
            val party : Party? = DS.parties.rtrieveParty(party_id)
            if (party == null)
                res = Response("Bye", StatusCode(false, "No Such Party Exists", null))
            else{
                val members : List<PartyUserResponse> = DS.memberships.getMembers(party_id)
                val partyRestaurant : List<PartyRestaurantResponse> = DS.memberships.partyRestaurants(party_id)
                val isMember : Boolean = DS.memberships.isInParty(user, party)
                res = Response(arrayListOf(members, partyRestaurant, isMember), StatusCode(true, "Success", user.username))
            }
        }
        ctx.json(res)
    }
    
    app.post("/api/memberships/:party_id"){ctx -> 
        val party_id : Int = ctx.pathParam("party_id").toInt()
        val username : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(username)
        val party : Party? = DS.parties.rtrieveParty(party_id)
        val res : Response
        if(user == null){
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        }
        else{
            if(party == null){
                res = Response("Bye", StatusCode(false, "No Such Party Exists", null))
            }
            else{
                val member: Int? = DS.memberships.createMembership(user, party)
                if (member != -1) {
                    res = Response(member, StatusCode(true, "Join Successful", user.username))
                }
                else{
                    res = Response(null, StatusCode(false, "Party is Full", null))
                }
            }
        }
        ctx.json(res)
    }
    
    app.delete("api/memberships/:party_id"){ctx ->
        val party_id : Int = ctx.pathParam("party_id").toInt()
        val username : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(username)
        val party : Party? = DS.parties.rtrieveParty(party_id)
        val res : Response
        if(user == null || party == null){
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        }
        else{
            val member: Int = DS.memberships.deleteMembership(user, party)
            if (member != -1)
                res = Response(member, StatusCode(true, "Leave Successful", user.username))
            else{
                res = Response(null, StatusCode(false, "You are not in the Party", null))
            }
        }
        ctx.json(res)
    }

    app.get("/api/statistics"){ctx ->
        val uid : Int? = ctx.sessionAttribute<Int>("username")
        val user : User? = DS.users.retrieveUser(uid)
        val res : Response
        if(user == null)
            res = Response(null, StatusCode(false, "Not Authenticated", null))
        else{
            val statistics : StatisticsResponse = DS.statistics.statistics()
            res = Response(statistics, StatusCode(true, "Successful", user.username))
        }
        ctx.json(res)
    }

}