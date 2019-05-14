package datamodels.cuisines

import org.jetbrains.exposed.dao.IntIdTable

object Cuisines : IntIdTable() {
    val cuisine = varchar("cuisine", 60)
}