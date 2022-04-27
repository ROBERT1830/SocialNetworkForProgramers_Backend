package robertconstantin.example.data.requests

/**
 * This data class will be used to either perfom post to follow or
 * delete fto unfollow with 2 different routes
 */
data class FollowUpdateRequest(
    //we dont need this because the followingUserId is the user that makes the request. Is who follows someone of rexample
    //so because is the same id of the userId who makes the request we can delete it.
    //val followingUserId: String,
    val followedUserId: String,
)
