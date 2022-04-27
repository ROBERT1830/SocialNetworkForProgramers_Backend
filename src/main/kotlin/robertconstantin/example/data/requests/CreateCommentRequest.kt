package robertconstantin.example.data.requests

data class CreateCommentRequest(
    val comment: String,
    val postId: String,
    //There is no reason to attatch the user id.
    /*Because shoudl the user explicetly attatch the id to create a comment.
    * Because the id can also be attatched to the JWT token
    * so que can extract it server side. So we dont need to attatch this as an extra.
    * Instead we can extract it from the token.
    * For now we have the email attatched to the token.
    * So instead of attatch the user email we will attatch the id
    * So with every authenticated request we will automatically will know
    * whic user made that rrequest. */
//    val userId: String
)
