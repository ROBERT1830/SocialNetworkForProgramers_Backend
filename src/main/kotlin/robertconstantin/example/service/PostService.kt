package robertconstantin.example.service

import robertconstantin.example.data.models.Post
import robertconstantin.example.data.repository.post.PostRepository
import robertconstantin.example.data.requests.CreatePostRequest
import robertconstantin.example.util.Constants.DEFAULT_POST_PAGE_SIZE

class PostService(
    private val postRepository: PostRepository
)
 {

    suspend fun createPostIfUserExists(request: CreatePostRequest): Boolean{
        return postRepository.createPostIfUserExists(
            Post(
                imageUrl = "",
                userId = request.userId,
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
}
