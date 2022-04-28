package robertconstantin.example.data.repository.post

import robertconstantin.example.data.models.Post

interface PostRepository {

    suspend fun createPostIfUserExists(post: Post): Boolean
    suspend fun deletePost(postId: String)

    //function that retrieves th post by those people we follow
    suspend fun getPostsByFollows(
        userId: String,
        page: Int  = 0,
        pageSize: Int
    ): List<Post>

    suspend fun getPostForProfile(
        userId: String,
        page: Int  = 0,
        pageSize: Int
    ): List<Post>

    suspend fun getPost(postId: String): Post?


}