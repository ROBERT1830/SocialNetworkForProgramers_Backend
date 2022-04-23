package robertconstantin.example.data.repository.follow

interface FollowRepository {

    /**To follow someone we should do it by email to search the user in the db and add him a
     * followed. The email is unique in the db
     *
     * Email is sensitive---> If you have the email of a user you know which user that is
     * so if you se the request for some reason you know which user that request actually belongs
     * to you. If you just have the id you have no idea. Only if you know he databse. And shouldn't be the case.
     * */
    suspend fun followUserIfExists(
        //the user who follows. This user who performs the follow or unfollow action. Current user
        followingUserId: String,
        //who follow the above user. Follow user. El que quiero segur
        followedUserId: String
    ): Boolean

    suspend fun unFollowUserIfExists(
        followingUserId: String,
        followedUserId: String
    ): Boolean
}