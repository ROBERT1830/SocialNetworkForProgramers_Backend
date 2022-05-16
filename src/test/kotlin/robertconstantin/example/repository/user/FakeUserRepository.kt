package robertconstantin.example.repository.user

import robertconstantin.example.data.models.User
import robertconstantin.example.data.repository.user.UserRepository
import robertconstantin.example.data.requests.UpdateProfileRequest

/**
 * Here we have created a fake version of our UserRepositryimpl that behavies the same way but just for singl etest case
 *
 * So her ewe will have a list in which we manage our databasekind of. And after each test case the
 * kind of database taht we simulate here will be empty again.
 *
 *
 */
class FakeUserRepository: UserRepository {
    //this mutable list will simulate the database.
    val users = mutableListOf<User>()


    override suspend fun createUser(user: User) {
        users.add(user)
    }

    override suspend fun getUserById(id: String): User? {
        return users.find {
            it.id == id
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    override suspend fun updateUser(
        userId: String,
        profileImageUrl: String?,
        bannerUrl: String?,
        updateProfileRequest: UpdateProfileRequest
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun doesPasswordForUserMatch(email: String, enteredPassword: String): Boolean {

        val user = getUserByEmail(email)
        return user?.password == enteredPassword
    }

    override suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean {
    }

    override suspend fun searchForUsers(query: String): List<User> {

    }

    override suspend fun getUsers(userIds: List<String>): List<User> {

    }
}