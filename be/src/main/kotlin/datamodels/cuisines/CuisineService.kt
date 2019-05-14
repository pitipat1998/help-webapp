package datamodels.cuisines

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class CuisineService(val dbConn: Connection){

    init {
        createTable()
    }

    private fun createTable(){
        transaction { SchemaUtils.create(Cuisines) }
    }

     fun allCuisines() : List<CuisineResponse> {
            return transaction { return@transaction Cuisine.all().map { c ->
                CuisineResponse(
                        id = c.id.value,
                        cuisine = c.cuisine
                )
            } }
    }

    fun createCuisine(cn: String) : Int{
        return transaction {
            val id = Cuisine.new {
                cuisine = cn
            }.id.value
            return@transaction id
        }
    }
}
