package robertconstantin.example.data.repository.post

import org.litote.kmongo.`in`
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import robertconstantin.example.data.models.Following
import robertconstantin.example.data.models.Post
import robertconstantin.example.data.models.User
import robertconstantin.example.util.Constants.DEFAULT_POST_PAGE_SIZE

class PostRepositoryImpl(
    db: CoroutineDatabase
): PostRepository {

    private val posts = db.getCollection<Post>()
    private val following = db.getCollection<Following>()
    private val users = db.getCollection<User>()

    override suspend fun createPostIfUserExists(post: Post): Boolean {
        //check if the user that want to make a post exists in user collection
        val doesUserExists = users.findOneById(post.userId) != null
        if (!doesUserExists){
            return false
        }
        posts.insertOne(post)
        return true
    }

    override suspend fun deletePost(postId: String) {
        posts.deleteOneById(postId)
    }

    /*For this we need pagination*/
    override suspend fun getPostsByFollows(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<Post> {

        /**We need a query to get all post by those people a given user follows.
         * So we need acces to following collection*/

        //get p
        val userIdsFromFollows = following.find(
            Following::followingUserId eq userId
            //to retrieve a list of them because by default the retun type is CoroutinePublisher
        ).toList().map {
            it.followedUserId
        }

        /**
         * First what we did was to get all Following collections that matched my id with the followingUseriD.
         * Then from those collections retrieve the followedUserId in order to get all post by this id.
         * So find all post from all people that is in the mapped list. Order the post by timestamp
         *
         * Also we want to limit how many results we want to get
         */

        val postsFromFollows = posts.find(
            Post::userId `in` userIdsFromFollows
        )
                //if we get the page 1 que skip 15 entries
            .skip(page * pageSize)
            .limit(pageSize) //we onluy want 15 elements at once.
            .descendingSort(Post::timestamp) //order descending because we want that those post made eariel to appear first
            .toList()

        return postsFromFollows

    }
}



































