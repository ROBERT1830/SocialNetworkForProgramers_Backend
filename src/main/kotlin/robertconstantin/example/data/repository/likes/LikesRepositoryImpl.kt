package robertconstantin.example.data.repository.likes

import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import robertconstantin.example.data.models.Comment
import robertconstantin.example.data.models.Like
import robertconstantin.example.data.models.Post
import robertconstantin.example.data.models.User
import robertconstantin.example.data.models.util.ParentType

class LikesRepositoryImpl(
    db: CoroutineDatabase
): LikesRepository {

    //the collection where we perfom actions
    private val likes = db.getCollection<Like>()
    private val users = db.getCollection<User>()
    //acces the parents because we can have likes of comments and posts
    private val comments = db.getCollection<Comment>()
    private val posts = db.getCollection<Post>()


    /**
     * To like a post just check if the post and the user exists
     * userId: the user that I tap like to its post
     *
     * likeParent ---> is a like to a specific post. parentId is the post Id because
     * inside a post we ahve comments for example. And those comments can be liked ass weel-
     * so for that is called likeParent
     */
    override suspend fun likeParent(userId: String, parentId: String, parentType: Int): Boolean { //return true if there is insertion
        val doesUserExist = users.findOneById(userId) != null

        //if user exists add and entry to the likes collection
        return if (doesUserExist){
            //find the parent and increase the like count
            when(parentType){
                ParentType.Post.type ->{
                    val post = posts.findOneById(parentId) ?: return false
                    posts.updateOneById(
                        id = parentId,
                        update = setValue(Post::likeCount, post.likeCount + 1)
                    )

                }
                ParentType.Comment.type ->{
                    val comment = comments.findOneById(parentId) ?: return false
                    comments.updateOneById(
                        id = parentId,
                        update = setValue(Comment::likeCount, comment.likeCount + 1)
                    )
                }
            }
            likes.insertOne(
                //insert an object which is a post object.
                Like(userId = userId, parentId = parentId, parentType, System.currentTimeMillis())
            )
            true
        }else{
            false
        }
    }

    override suspend fun unlikeParent(userId: String, parentId: String, parentType: Int): Boolean {
        val doesUserExist = users.findOneById(userId) != null
        //if user exists add and entry to the likes collection
        return if (doesUserExist){
            //find the parent and increase the like count
            when(parentType){
                ParentType.Post.type ->{
                    val post = posts.findOneById(parentId) ?: return false
                    posts.updateOneById(
                        id = parentId,
                        update = setValue(Post::likeCount, (post.likeCount - 1).coerceAtLeast(0)) //the coerce will asign the value but if it is les thatn 0 will asign allways 0
                    )

                }
                ParentType.Comment.type ->{
                    val comment = comments.findOneById(parentId) ?: return false
                    comments.updateOneById(
                        id = parentId,
                        update = setValue(Comment::likeCount, (comment.likeCount - 1).coerceAtLeast(0))
                    )
                }
            }
            //delete one where the userId and postid is equel to the parameters.
            //you are deleting a document that has as userId and postId those specified by parameters.
            likes.deleteOne(
                and(
                    Like::userId eq userId,
                    Like::parentId eq parentId
                )
            )
            true
        }else{
            false
        }
    }

    override suspend fun deleteLikesForParent(parentId: String) {
        //When we delete a post we will delete all the likes that has the postId we passed.
        likes.deleteMany(Like::parentId eq parentId)
    }

    override suspend fun getLikesForParent(parentId: String, page:Int, pageSize: Int): List<Like> {

        return likes.find(Like::parentId eq parentId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Like::timestamp)
            .toList()
    }
}
































