package robertconstantin.example.data.repository.post

import robertconstantin.example.data.models.Post
import robertconstantin.example.data.responses.PostResponse

interface PostRepository {

    suspend fun createPost(post: Post): Boolean
    suspend fun deletePost(postId: String)

    //function that retrieves th post by those people we follow
    suspend fun getPostsByFollows(
        ownUserId: String,
        page: Int  = 0,
        pageSize: Int
    ): List<PostResponse>

    suspend fun getPostForProfile(
        ownUserId:String,
        userId: String,
        page: Int  = 0,
        pageSize: Int
    ): List<PostResponse>

    suspend fun getPost(postId: String): Post?

    suspend fun getPostDetails(userId:String, postId:String): PostResponse?


}