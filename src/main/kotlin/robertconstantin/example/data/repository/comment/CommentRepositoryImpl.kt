package robertconstantin.example.data.repository.comment

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import robertconstantin.example.data.models.Comment

class CommentRepositoryImpl(
    db: CoroutineDatabase
): CommentRepository {

    val comments = db.getCollection<Comment>()


    override suspend fun createComment(comment: Comment): String {
        comments.insertOne(comment)
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

    override suspend fun getCommentsForPost(postId: String): List<Comment> {
        //we have in comment document the postId
        return comments.find(Comment::postId eq postId).toList()
    }

    override suspend fun getComment(commentId: String): Comment? {
        return comments.findOneById(Comment::id eq commentId)
    }



}