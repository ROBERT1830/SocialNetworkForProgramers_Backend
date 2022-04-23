package robertconstantin.example.data.requests

/**
 * This data class will be used to either perfom post to follow or
 * delete fto unfollow with 2 different routes
 */
data class FollowUpdateRequest(
    val followingUserId: String,
    val followedUserId: String,
)
