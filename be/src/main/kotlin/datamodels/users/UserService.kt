package datamodels.users

import java.sql.Connection
import java.security.MessageDigest
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.mindrot.jbcrypt.*

const val pepper = "f6b987e66836c8e8e10e058718b3202ce6df6af50be74e9e50f40f106c2376e8c15da15a0ed027467e1f5f1a7e3dd80fc751c487603fbb935644e1c7fbe8e55e"

class UserService(private val dbConn: Connection) {

    init {
        createTables()
    }

    private fun createTables(){
        transaction {
            SchemaUtils.create(Users, Salts)
        }
    }

    private fun hashSHA256(st: String) : String{
        val md : MessageDigest = MessageDigest.getInstance("SHA-256")
        md.update(st.toByteArray())
        return md.digest().map{ elt -> String.format("%02X", elt)}.reduce { acc, elt -> acc+elt}
    }

    private fun retrieveSalt(userIn: String) : String? {
         val query : List<Salt> = transaction {
             return@transaction Salt.find{
                 Salts.userIn.eq(userIn)
             }.toList()
        }
        if(query.size > 0){
            return query[0].salt
        }
        return null
    }

    private fun isInDB(username: String) : Int? {
        val query : List<User> = transaction {
             return@transaction User.find{
                 Users.username.eq(username)
             }.toList()
        }
        if(query.size > 0){
            return query[0].id.value
        }
        return null
    }

    fun retrieveUser(uid : Int?) : User? {
        if(uid == null) return null
        return transaction {
            return@transaction User.findById(uid)
        }
    }

    fun userRegistration(un: String, pswd: String, em: String, addr: String) : User? {

        if (isInDB(un) != null){
            return null
        }

        val s : String = BCrypt.gensalt(12)
        val ui : String = hashSHA256(un + pepper)
        val spswd : String = BCrypt.hashpw(pswd + pepper, s)

        return transaction {
            Salt.new {
                userIn = ui
                salt = s
            }
            return@transaction User.new {
                username = un
                password = spswd
                email = em
                address = addr
                lastLogin = DateTime.now()
                createAt = DateTime.now()
            }
        }
    }

    fun userLogin(username: String, password: String) : User? {

        val uid : Int? = isInDB(username)
        if (uid == null){
            return null
        }

        val userIn : String = hashSHA256(username+ pepper)
        val salt : String? = retrieveSalt(userIn)
        val user : User? = retrieveUser(uid)
        if (salt == null || user == null){
            return null
        }

        if(BCrypt.checkpw(password+ pepper, user.password)){
            transaction{
                user.lastLogin = DateTime.now()
            }
            return user
        }
        return null
    }

    fun userProfile(id: Int) : UserResponse?{
        val user = transaction {
            return@transaction User.findById(id)
        }
        if (user == null)
            return null
        return UserResponse(user.username, user.email, user.address, user.lastLogin.toLocalDateTime().toString())
    }
}
