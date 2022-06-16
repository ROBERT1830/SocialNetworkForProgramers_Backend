package robertconstantin.example.service

import robertconstantin.example.data.models.Post
import robertconstantin.example.data.repository.post.PostRepository
import robertconstantin.example.data.requests.CreatePostRequest
import robertconstantin.example.data.responses.PostResponse
import robertconstantin.example.util.Constants.DEFAULT_POST_PAGE_SIZE

class PostService(
    private val postRepository: PostRepository
)
 {
    //userid: comes from token
    suspend fun createPost(request: CreatePostRequest, userId: String, imageUrl:String): Boolean{
        return postRepository.createPost(
            Post(
                imageUrl = imageUrl,
                userId = userId,
                timestamp = System.currentTimeMillis(),
                description = request.description
            )
        )
    }

     suspend fun getPostForFollows(
         ownUserId: String,
         page: Int,
         pageSize: Int = DEFAULT_POST_PAGE_SIZE
     ): List<PostResponse>{
         return postRepository.getPostsByFollows(
             ownUserId, page, pageSize
         )
     }


     suspend fun getPostForProfile(
         ownUserId: String,
         userId: String,
         page: Int,
         pageSize: Int = DEFAULT_POST_PAGE_SIZE
     ): List<PostResponse>{
         return postRepository.getPostForProfile(
             ownUserId,userId, page, pageSize
         )
     }

     suspend fun getPost(
         postId: String,
     ): Post? {
         return postRepository.getPost(postId)
     }

     suspend fun getPostDetails(ownUserId:String, postId:String): PostResponse? {
         return postRepository.getPostDetails(ownUserId, postId)
     }

     suspend fun deletePost(postId: String){
         postRepository.deletePost(postId)
     }
}
