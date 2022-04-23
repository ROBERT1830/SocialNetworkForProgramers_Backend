package robertconstantin.example.repository.user

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import robertconstantin.example.data.models.User

/**
 * Here we have a simple mvc pattern where the controller access the database
 * so in its constructor needs that database.
 *
 */
class UserRepositoryImpl(
    private val db: CoroutineDatabase
): UserRepository {

    //we need to have access to the collecitons that are gona be used in the db.
    //For creating a user we need the user Collection
    private val users = db.getCollection<User>()

    override suspend fun createUser(user: User) {
        //insert a user in mongo
        users.insertOne(user)

    }

    override suspend fun getUserById(id: String): User? {
        //findOneById return a nullable object. So make the return type nullable.
        return users.findOneById(id)
    }


    override suspend fun getUserByEmail(email: String): User? {
        //here fin one, not user the id
        //Finds the first document that match the filter in the collection
        //for the filter user the document we want to look in and more specifically the variable
        return users.findOne(User::email eq email)
    }
}