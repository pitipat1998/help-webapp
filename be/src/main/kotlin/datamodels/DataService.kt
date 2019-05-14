package datamodels

import datamodels.cuisines.CuisineService
import datamodels.memberships.MembershipService
import datamodels.parties.PartyService
import datamodels.restaurants.RestaurantService
import datamodels.reviews.ReviewService
import datamodels.statistics.StatisticsService
import datamodels.users.UserService
import org.jetbrains.exposed.sql.Database
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class DataService(private val connectionString: String, private val dbName : String, private val connectionProperties: Properties) {
    init {
        val dbConn = DriverManager.getConnection(connectionString, connectionProperties)
        createDB(dbConn)
        dbConn.close()
    }
    private fun createDB(dbConn:Connection){
        val sql = """
            CREATE DATABASE IF NOT EXISTS $dbName;
        """.trimIndent()
        val stmt = dbConn.createStatement()
        stmt.executeUpdate(sql)
    }

    private val url = connectionString + dbName
    private val dbConn = DriverManager.getConnection(url, connectionProperties)
    private val ormConn = Database.connect(url, driver = "com.mysql.cj.jdbc.Driver",
                                            user = connectionProperties.getProperty("user"), password = connectionProperties.getProperty("password"))
    val users = UserService(dbConn)
    val restaurants = RestaurantService(dbConn)
    val reviews = ReviewService(dbConn)
    val cuisines = CuisineService(dbConn)
    val parties = PartyService(dbConn)
    val memberships = MembershipService(dbConn)
    val statistics = StatisticsService(dbConn)

}
