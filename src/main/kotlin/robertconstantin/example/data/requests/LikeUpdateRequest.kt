package robertconstantin.example.data.requests

/**
 * Here we could think to add a boolena to check if the user performs like or unlike,
 * but we will nod access the same route for both actions. So no need that.
 */
data class LikeUpdateRequest(
    //the user that likes the post
    val userId: String,
    //liked post
    val parentId: String
)
