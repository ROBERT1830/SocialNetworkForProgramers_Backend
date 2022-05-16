package robertconstantin.example.data.repository.user

import org.litote.kmongo.`in`
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.or
import org.litote.kmongo.regex
import robertconstantin.example.data.models.User
import robertconstantin.example.data.requests.UpdateProfileRequest

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

    override suspend fun updateUser(
        userId: String,
        profileImageurl : String?,
        bannerUrl:String?,
        updateProfileRequest: UpdateProfileRequest): Boolean {
        //get default data from a given user
        val user = getUserById(userId) ?: return false


        //perform an update in mongo
        return users.updateOneById(
            //find the user to update using the userId we pass
            id = userId,
            //which collection we want to update? User collection. Which data?
            update = User(
                email = user.email,
                username = updateProfileRequest.userName,
                password = user.password,
                profileImageUrl = profileImageurl ?: user.profileImageUrl, //only update the values it the profile image url is not null
                bannerUrl = bannerUrl ?: user.bannerUrl,
                bio = updateProfileRequest.bio,
                githubUrl = updateProfileRequest.githubUrl,
                instagramUrl = updateProfileRequest.instagramUrl,
                linkedInUlr = updateProfileRequest.linkedInUrl,
                skills = updateProfileRequest.skills,
                //those dont chang
                followingCount = user.followingCount,
                followerCount = user.followerCount,
                postCount = user.postCount,
                id = user.id
            )
        ).wasAcknowledged()
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

    /**
     * Searching in mongo requires regular expressions.
     * If the query is contained in the user name nad not distinguish between lower and upper.
     * (?i) ---> regular expresion for match lower and upper
     * We use here a regex like contains in normal query or like %xxx%
     *
     *
     */
    override suspend fun searchForUsers(query: String): List<User> {
        return users.find(
            or(User::username regex  Regex("(?i).*$query.*"),
                User::email eq query //if you want to search by email will work ass well. but need the full email because here we dont use regex.
            )
        )
            .descendingSort(User::followerCount) //higher number of follower appear before
            .toList()

    }

    override suspend fun getUsers(userIds: List<String>): List<User> {
        //find where
        return users.find(User::id `in` userIds).toList()

    }
}



































