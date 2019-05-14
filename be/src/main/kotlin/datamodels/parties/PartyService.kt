package datamodels.parties

import datamodels.memberships.Membership
import datamodels.memberships.Memberships
import datamodels.restaurants.PartyRestaurantResponse
import datamodels.users.User
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class PartyService(val dbConn: Connection){

    init{
        createTable()
    }

    private fun createTable(){
        transaction { SchemaUtils.create(Parties) }
    }

    fun createParty(user: User, n: String) : Int {
        return transaction {
            val party =  Party.new{
                name = n
                noMembers = 1
            }
            Membership.new {
                partyID = party
                userID = user
            }
           return@transaction party.id.value
        }
    }

    fun rtrieveParty(pid : Int) : Party? {
        return transaction {
            val party = Party.findById(pid)
            if (party == null) return@transaction null
            return@transaction party
        }
    }

    fun allParties() : List<PartyResponse> {
        return transaction { Party.all().map { party ->
            PartyResponse(
                    id = party.id.value,
                    name = party.name,
                    noMembers = party.noMembers
            )
        } }
    }

    fun userJoinedParties(uid: Int) : List<PartyResponse> {
        return transaction {
            Membership.find{
                Memberships.userID eq uid
            }.map { membership ->
                PartyResponse(
                        id = membership.partyID.id.value,
                        name = membership.partyID.name,
                        noMembers = membership.partyID.noMembers
                )
            }
        }
    }

    fun userUnjoinedParties(uid: Int) : List<PartyResponse> {
        val sql = """
            with user_parties as (
              select distinct party_id
              from Memberships
                inner join Parties P on Memberships.party_id = P.id
              where Memberships.user_id = ?
            ),
            all_parties as (
              select * from Parties
            )
            select * from all_parties where id not in (select * from user_parties)
        """.trimIndent()
        val prepStmt = dbConn.prepareStatement(sql)
        with(prepStmt){
            setInt(1, uid)
        }
        val rs = prepStmt.executeQuery()
        val results = ArrayList<PartyResponse>()
        while (rs.next()){
            results.add(PartyResponse(
                    id = rs.getInt("id"),
                    name = rs.getString("name"),
                    noMembers = rs.getInt("no_members")
            ))
        }
        return results
    }


}
