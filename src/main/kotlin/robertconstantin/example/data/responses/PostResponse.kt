package robertconstantin.example.data.responses

data class PostResponse(
       //needed to pass in nav arguments
    val id: String,
    val userId: String,
    //in backend we know user id and then we can get the name
    val username: String,
    //from server
    val imageUrl: String,
    val profilePicture: String,
    val description: String,
    val likeCount: Int,
    val commentCount: Int,
    val isLiked: Boolean

)
