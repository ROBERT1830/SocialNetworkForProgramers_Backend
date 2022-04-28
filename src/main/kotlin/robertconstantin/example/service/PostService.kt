package robertconstantin.example.service

import robertconstantin.example.data.models.Post
import robertconstantin.example.data.repository.post.PostRepository
import robertconstantin.example.data.requests.CreatePostRequest
import robertconstantin.example.util.Constants.DEFAULT_POST_PAGE_SIZE

class PostService(
    private val postRepository: PostRepository
)
 {
    //userid: comes from token
    suspend fun createPostIfUserExists(request: CreatePostRequest, userId: String): Boolean{
        return postRepository.createPostIfUserExists(
            Post(
                imageUrl = "",
                userId = userId,
                timestamp = System.currentTimeMillis(),
                description = request.description
            )
        )
    }

     suspend fun getPostForFollows(
         userId: String,
         page: Int,
         pageSize: Int = DEFAULT_POST_PAGE_SIZE
     ): List<Post>{
         return postRepository.getPostsByFollows(
             userId, page, pageSize
         )
     }

     suspend fun getPostForProfile(
         userId: String,
         page: Int,
         pageSize: Int = DEFAULT_POST_PAGE_SIZE
     ): List<Post>{
         return postRepository.getPostForProfile(
             userId, page, pageSize
         )
     }

     suspend fun getPost(postId: String): Post? = postRepository.getPost(postId)

     suspend fun deletePost(postId: String){
         postRepository.deletePost(postId)
     }
}
