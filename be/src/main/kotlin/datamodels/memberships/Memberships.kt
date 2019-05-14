package datamodels.memberships

import datamodels.parties.Parties
import datamodels.users.Users
import org.jetbrains.exposed.dao.IntIdTable

object Memberships : IntIdTable() {
    val partyID = reference( "party_id", Parties)
    val userID = reference("user_id", Users)
}
