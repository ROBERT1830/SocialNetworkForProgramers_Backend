package robertconstantin.example.service

import robertconstantin.example.data.models.Comment
import robertconstantin.example.data.repository.comment.CommentRepository
import robertconstantin.example.data.requests.CreateCommentRequest
import robertconstantin.example.util.Constants

class CommentService(
    private val repository: CommentRepository
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

        repository.createComment(
            Comment(
                comment = createCommentRequest.comment,
                userId = userId,
                postId = createCommentRequest.postId,
                timestamp = System.currentTimeMillis()
            )
        )
        return ValidationEvent.Success
    }

    suspend fun deleteComment(commentId: String): Boolean {
        return repository.deleteComment(commentId)
    }

    suspend fun getCommentsForPost(postId: String): List<Comment>{
        return repository.getCommentsForPost(postId)
    }

    suspend fun getCommentById(commentId: String): Comment? {
        return repository.getComment(commentId)
    }

    sealed class ValidationEvent {
        object ErrorFieldEmpty: ValidationEvent()
        object ErrorCommentTooLong: ValidationEvent()
        object Success: ValidationEvent()
    }
}





















