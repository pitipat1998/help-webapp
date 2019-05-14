package datamodels.memberships

import datamodels.parties.Parties
import datamodels.parties.Party
import datamodels.users.User
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class Membership(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<Membership>(Memberships)
    var partyID by Party referencedOn Memberships.partyID
    var userID by User referencedOn Memberships.userID
}
