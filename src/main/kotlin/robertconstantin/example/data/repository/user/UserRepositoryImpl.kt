package robertconstantin.example.data.repository.user

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

    //get the user by emailand check if the pasword matches when the user login
    override suspend fun doesPasswordForUserMatch(email: String, enteredPassword: String): Boolean {

        val user = getUserByEmail(email) //get unique user for email
        return user?.password == enteredPassword
    }

    /*email of the user that is performing the server request
    * userId: id of the user hat is performing the server request.
    *
    * We need to check if the email of the user that makes the request is actually using
    * its own email and not other.
    * By doing so get the collection form the db that matches the user id and then compare
    * the email from that colelction to the email passed in.
    * In that way we prevent someone trying to make apost for example by using an email of an ohter
    * user.
    * */
    override suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean {
        /*Find the user with the id we pass, and we want to make sure that the email is equeal to
        * the email we pass as a parameter
        *
        * So we check that the user with that id has the same email as the user who made the request*/
        return users.findOneById(userId)?.email == email
    }
}



































