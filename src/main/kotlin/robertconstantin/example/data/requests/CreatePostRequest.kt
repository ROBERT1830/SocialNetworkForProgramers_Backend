package robertconstantin.example.data.requests

data class CreatePostRequest(
    //Is not needed anymre ot get it from the client because now we user the token to get the email.
    //val userId: String,
    val description: String
)