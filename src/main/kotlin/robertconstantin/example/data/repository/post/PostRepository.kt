package robertconstantin.example.data.repository.post

import robertconstantin.example.data.models.Post

interface PostRepository {

    suspend fun createPostIfUserExists(post: Post): Boolean
    suspend fun deletePost(postId: String)

    suspend fun getPostsByFollows(
        userId: String,
        page: Int  = 0,
        pageSize: Int
    ): List<Post>

    //function that retrieves th post by those people we follow

}