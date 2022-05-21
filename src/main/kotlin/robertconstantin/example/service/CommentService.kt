package robertconstantin.example.service

import robertconstantin.example.data.models.Comment
import robertconstantin.example.data.repository.comment.CommentRepository
import robertconstantin.example.data.repository.user.UserRepository
import robertconstantin.example.data.requests.CreateCommentRequest
import robertconstantin.example.data.responses.CommentResponse
import robertconstantin.example.util.Constants

class CommentService(
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository
) {

    suspend fun createComment(createCommentRequest: CreateCommentRequest, userId: String): ValidationEvent{

        createCommentRequest.apply {
            if (comment.isBlank() /*|| userId.isBlank()*/ || postId.isBlank()){
                return ValidationEvent.ErrorFieldEmpty
            }
            if (comment.length > Constants.MAX_COMMENT_LENGTH){
                ValidationEvent.ErrorCommentTooLong
            }
        }

        val user = userRepository.getUserById(userId) ?: return ValidationEvent.UserNotFount

        commentRepository.createComment(
            Comment(
                username = user.username,
                profilePictureUrl = user.profileImageUrl,
                likeCount = 0,
                comment = createCommentRequest.comment,
                userId = userId,
                postId = createCommentRequest.postId,
                timestamp = System.currentTimeMillis()
            )
        )
        return ValidationEvent.Success
    }

    suspend fun deleteCommentsForPost(postId: String){
        commentRepository.deleteCommentsFromPost(postId)
    }

    suspend fun deleteComment(commentId: String): Boolean {
        return commentRepository.deleteComment(commentId)
    }

    suspend fun getCommentsForPost(postId: String, ownUserId: String): List<CommentResponse>{
        return commentRepository.getCommentsForPost(postId, ownUserId)
    }

    suspend fun getCommentById(commentId: String): Comment? {
        return commentRepository.getComment(commentId)
    }

    sealed class ValidationEvent {
        object ErrorFieldEmpty: ValidationEvent()
        object ErrorCommentTooLong: ValidationEvent()
        object UserNotFount: ValidationEvent()
        object Success: ValidationEvent()
    }
}





















