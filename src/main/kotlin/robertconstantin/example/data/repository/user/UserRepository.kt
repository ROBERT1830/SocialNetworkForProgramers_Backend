package robertconstantin.example.data.repository.user

import robertconstantin.example.data.models.User

/**
 * This is very usefull for testing. Lets say we wanto to test this user userController. Then
 * in our use cases we dont talk to the actuall db. We want to kind simulate that which is much quicker and
 * we dont wanto to make changes in the real db by doing test cases. That is bad. So with this
 * interface we can still pass a user controller in our repo letter on and we can give this functions
 * our own definitions. For example we can just keep track of a hassmap or a list that just keeps track of all
 * our users just for the test case. And with tat we can see if everything is fines in other circumstances.
 */
interface UserRepository {
    //for create an account
    suspend fun createUser(user: User)
    //for check for an account
    suspend fun getUserById(id: String): User?

    suspend fun getUserByEmail(email: String): User?

    //function that gets a user and checks if the paswword is what the user entered
    suspend fun doesPasswordForUserMatch(email:String, enteredPassword: String): Boolean

    //to verify that the email from whom is gona create a post belong to user id.
    suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean

    suspend fun searchForUsers(query: String): List<User>

}