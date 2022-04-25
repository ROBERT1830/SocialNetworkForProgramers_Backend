package robertconstantin.example.data.repository.comment

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import robertconstantin.example.data.models.Comment

class CommentRepositoryImpl(
    db: CoroutineDatabase
): CommentRepository {

    val comments = db.getCollection<Comment>()


    override suspend fun createComment(comment: Comment) {
        comments.insertOne(comment)

    }

    override suspend fun deleteComment(commentId: String): Boolean {

        val deleteCount = comments.deleteOneById(commentId).deletedCount
        return deleteCount > 0
    }

    override suspend fun getCommentsForPost(postId: String): List<Comment> {
        //we have in comment document the postId
        return comments.find(Comment::postId eq postId).toList()
    }

    override suspend fun getComment(commentId: String): Comment? {
        return comments.findOneById(Comment::id eq commentId)
    }



}