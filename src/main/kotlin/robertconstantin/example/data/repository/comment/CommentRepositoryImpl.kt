package robertconstantin.example.data.repository.comment

import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import robertconstantin.example.data.models.Comment
import robertconstantin.example.data.models.Like
import robertconstantin.example.data.models.Post
import robertconstantin.example.data.responses.CommentResponse

class CommentRepositoryImpl(
    db: CoroutineDatabase
): CommentRepository {

    private val posts = db.getCollection<Post>()
    private val comments = db.getCollection<Comment>()
    private val likes = db.getCollection<Like>()


    override suspend fun createComment(comment: Comment): String {
        comments.insertOne(comment)
        //increase comment number
        val oldCommentCount = posts.findOneById(comment.postId)?.commentCount ?: 0
        posts.updateOneById(comment.postId, setValue(Post::commentCount, oldCommentCount + 1))
        return comment.id

    }

    override suspend fun deleteComment(commentId: String): Boolean {

        val deleteCount = comments.deleteOneById(commentId).deletedCount
        return deleteCount > 0
    }

    override suspend fun deleteCommentsFromPost(postId: String): Boolean {
        return comments.deleteMany(
            Comment::postId eq postId
        ).wasAcknowledged() // returns true if the write in the db was successfull.
    }

    override suspend fun getCommentsForPost(postId: String, ownUserId: String): List<CommentResponse> {
        //we have in comment document the postId
        return comments.find(Comment::postId eq postId).toList().map{ comment ->
            /*Find where the parent id is the comment id, and the usre id is the userId of the comment*/
            val isLiked = likes.findOne(
                and(
                    Like::userId eq ownUserId, //user that makes the response if the followingUserId
                    Like::parentId eq comment.id
                )
            ) != null //if we find a document in like collections means that there is a like for that comment

            CommentResponse(
                id = comment.id,
                username = comment.username,
                profileImageUrl = comment.profilePictureUrl,
                timestamp = comment.timestamp,
                comment = comment.comment,
                isLiked = isLiked,
                likeCount = comment.likeCount,

            )
        }

    }

    override suspend fun getComment(commentId: String): Comment? {
        return comments.findOneById(Comment::id eq commentId)
    }



}