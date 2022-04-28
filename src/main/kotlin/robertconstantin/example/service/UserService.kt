package robertconstantin.example.service

import robertconstantin.example.data.models.User
import robertconstantin.example.data.repository.follow.FollowRepository
import robertconstantin.example.data.repository.user.UserRepository
import robertconstantin.example.data.requests.CreateAccountRequest
import robertconstantin.example.data.requests.LoginRequest
import robertconstantin.example.data.responses.UserResponseItem

class UserService(
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {
    suspend fun doesUserWithEmailExist(email: String): Boolean{
        return userRepository.getUserByEmail(email) != null
    }

    //userId comes from post that we obtained
    suspend fun doesEmailBelongToUserId(email: String, userId:String): Boolean{
        return userRepository.doesEmailBelongToUserId(email, userId)
    }

    fun validateCreateAccountRequest(request: CreateAccountRequest): ValidationEvent{

        //Cheack if the request is empty or not
        if (request.email.isBlank() || request.password.isBlank() || request.username.isBlank()){
           return ValidationEvent.ErrorFieldEmpty
        }
        return ValidationEvent.SuccessEvent
    }

    suspend fun getUserByEmail(email:String): User? {
        return userRepository.getUserByEmail(email)
    }
    fun isValidPassword(enteredPassword: String, actualPassword: String): Boolean{
        return enteredPassword == actualPassword
    }

    suspend fun doesPasswordMatchForUser(request: LoginRequest): Boolean{
       return userRepository.doesPasswordForUserMatch(
            email = request.email,
            enteredPassword = request.password
        )
    }

    /**
     * Find all followers of that user
     */
    suspend fun searchForUsers(query: String, userId: String): List<UserResponseItem>{
        //list of users from db that matches the query.
        val users = userRepository.searchForUsers(query)
        //get all following documents in which the current user appear. That means get all collections in which
        //current user follows someone.
        val followsByUser = followRepository.getFollowsbyUser(userId)

        //with that list we wanto to check for every single entry and determine if the user (current user)
        //is following the users in this list.
        return users.map { user ->
            /**
             * This is not optimal.... because for every single db we are making aquery. instead we
             * can just get a list of all people this user is following and then we can make a check withoiut
             * query the db. Only need one query.
             */
//            val isFollowing = followRepository.doesUserFollow(
//                followingUserId = userId, //current,
//                followedUserId = user.id
//            )
            //go through the list that contains the current user as followingUser and followedUserId
            //
            val isFollowing = followsByUser.find { it.followedUserId == user.id } != null //if ginf and is not null
            UserResponseItem(
                userName = user.username,
                profilePictureUrl = user.profileImageUrl,
                bio = user.bio,
                isFollowing = isFollowing
            )
        }

        //we need a function to check if a user with a given id follows a user with an other id.
    }


    suspend fun createUser(request: CreateAccountRequest){
        userRepository.createUser(
            User(
                email = request.email,
                username = request.username,
                password = request.password,
                profileImageUrl = "",
                bio = "",
                githubUrl = null,
                instagramUrl = null,
                linkedInUlr = null
            )
        )
    }

    /********************/
    sealed class ValidationEvent {
        object ErrorFieldEmpty: ValidationEvent()
        object SuccessEvent: ValidationEvent()
    }
}