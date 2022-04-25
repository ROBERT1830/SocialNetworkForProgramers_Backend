package robertconstantin.example.service

import robertconstantin.example.data.models.Post
import robertconstantin.example.data.repository.post.PostRepository
import robertconstantin.example.data.requests.CreatePostRequest

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
}
