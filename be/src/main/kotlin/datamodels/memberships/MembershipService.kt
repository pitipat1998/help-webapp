package datamodels.memberships

import datamodels.parties.Party
import datamodels.restaurants.PartyRestaurantResponse
import datamodels.restaurants.RestaurantResponse
import datamodels.restaurants.Restaurants
import datamodels.users.PartyUserResponse
import datamodels.users.User
import datamodels.users.UserResponse
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class MembershipService(val dbConn: Connection){

    init{
        createTable()
    }

    private fun createTable(){
        transaction { SchemaUtils.create(Memberships) }
    }

    fun createMembership(user: User, party: Party) : Int {
        if( isInParty(user, party)) return -1
        return transaction {
            if (party.noMembers >= 16) return@transaction -1
            val membership = Membership.new {
                partyID = party
                userID = user
            }
            party.noMembers += 1
            return@transaction  membership.id.value
        }
    }

    fun deleteMembership(user: User, party: Party) : Int {
        if (!isInParty(user, party)) return -1
        return transaction {
            val membership = Membership.find{
                (Memberships.userID eq user.id) and (Memberships.partyID eq party.id)
            }.toList()
            if (membership.size > 0) {
                val id = membership[0].id.value
                val party = membership[0].partyID
                membership[0].delete()
                if (party.noMembers <= 1){
                    party.delete()
                    return@transaction 0
                }
                else{
                    party.noMembers -= 1
                }
                return@transaction id
            }
            else return@transaction -1
        }
    }

    fun getMembers(pid: Int) : List<PartyUserResponse> {
        return transaction {
            Membership.find {
                Memberships.partyID eq pid
            }
                    .map{membership ->
                        PartyUserResponse(
                                username = membership.userID.username
                        )
                }.sortedWith(compareBy{it.username})
            }
    }
    fun isInParty(user:User, party: Party) : Boolean {
        return transaction {
            val membership = Membership.find{
                (Memberships.userID eq user.id) and (Memberships.partyID eq party.id)
            }.toList()
            if(membership.size > 0) return@transaction true
            return@transaction false
        }
    }

    fun partyRestaurants(pid : Int) : List<PartyRestaurantResponse> {
        val sql = """
            select  Restaurants.id, Restaurants.name, party_restaurant.party_avg
                   from (select restaurant_id, avg(rating) party_avg
            from (select distinct user_id from Memberships where party_id = ?) x inner join Reviews on x.user_id = Reviews.user_id
            group by restaurant_id) party_restaurant
              inner join  Restaurants on party_restaurant.restaurant_id = Restaurants.id
            order by party_restaurant.party_avg desc limit 5
        """.trimIndent()
        val prepStmt = dbConn.prepareStatement(sql)
        with(prepStmt){
            setInt(1, pid)
        }
        val rs = prepStmt.executeQuery()
        val result = ArrayList<PartyRestaurantResponse>()
        while(rs.next()){
            result.add(PartyRestaurantResponse(
                    id = rs.getInt("id"),
                    name = rs.getString("name"),
                    partyRating = rs.getDouble("party_avg")
            ))
        }
        return result
    }
}
