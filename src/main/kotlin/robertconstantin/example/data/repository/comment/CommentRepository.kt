package robertconstantin.example.data.repository.comment

import robertconstantin.example.data.models.Comment
import robertconstantin.example.data.responses.CommentResponse

sealed interface CommentRepository {

    suspend fun createComment(comment: Comment): String

    suspend fun deleteComment(commentId: String): Boolean

    suspend fun deleteCommentsFromPost(postId: String): Boolean

    suspend fun getCommentsForPost(postId: String, ownUserId: String): List<CommentResponse>

    suspend fun getComment(commentId: String): Comment?
}