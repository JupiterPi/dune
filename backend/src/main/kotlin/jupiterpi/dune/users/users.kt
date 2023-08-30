package jupiterpi.dune.users

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

object Users : IntIdTable() {
    val name = varchar("name", 50)
    val password = varchar("password", 50)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    var name by Users.name
    var password by Users.password
}

object UserRepo {
    fun createUser(credentials: UserCredentials) {
        transaction { User.new {
            name = credentials.name
            password = credentials.password
        } }
    }

    fun validateUser(credentials: UserCredentials): Boolean {
        return transaction { User.find { Users.name eq credentials.name and (Users.password eq credentials.password) }.count() == 1L }
    }

    fun changePassword(username: String, newPassword: String) {
        transaction { User.find { Users.name eq username }.single().password = newPassword }
    }
}