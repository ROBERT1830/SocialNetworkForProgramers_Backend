package robertconstantin.example.data.repository.post

import org.litote.kmongo.`in`
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import robertconstantin.example.data.models.Following
import robertconstantin.example.data.models.Like
import robertconstantin.example.data.models.Post
import robertconstantin.example.data.models.User
import robertconstantin.example.data.responses.PostResponse
import robertconstantin.example.util.Constants.DEFAULT_POST_PAGE_SIZE

class PostRepositoryImpl(
    db: CoroutineDatabase
) : PostRepository {

    private val posts = db.getCollection<Post>()
    private val following = db.getCollection<Following>()
    private val users = db.getCollection<User>()
    private val likes = db.getCollection<Like>()
    override suspend fun createPost(post: Post): Boolean {
        //check if the user that want to make a post exists in user collection
//        val doesUserExists = users.findOneById(post.userId) != null
//        if (!doesUserExists){
//            return false
//        }
        return posts.insertOne(post).wasAcknowledged()
    }

    override suspend fun deletePost(postId: String) {
        posts.deleteOneById(postId)
    }

    /*For this we need pagination
    *
    * Get from all following documents that matched our id (current user id, following). Then
    * map to get the list of followedUserId. With that ids now we will get all the post for each and every id
    *
    * Take the posts that have id of people we follow. Must have the id of the people you follow.
    * */
    override suspend fun getPostsByFollows(
        ownUserId: String,
        page: Int,
        pageSize: Int
    ): List<PostResponse> {

        /**We need a query to get all post by those people a given user follows.
         * So we need acces to following collection*/

        //get p
        val userIdsFromFollows = following.find(
            Following::followingUserId eq ownUserId
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
            .map {
                    post ->
                //see if a post is liked or not
                val isLiked = likes.findOne(
                    //the parent id of the like needs to be t he post and the other handf tghe userId of the like,. so who liked
                    // the parent id, need to be our user.
                    and(Like::parentId eq post.id,
                        Like::userId eq ownUserId)
                )?.also { println("Found like document: $it") } != null //here we debugg with println
                val user = users.findOneById(post.userId)
                PostResponse(
                    id = post.id,
                    userId = ownUserId,
                    username = user?.username ?:"",
                    imageUrl = post.imageUrl,
                    profilePicture = user?.profileImageUrl ?: "",
                    description = post.description,
                    likeCount = post.likeCount,
                    commentCount = post.commentCount,
                    isLiked = isLiked

                )
            }

        return postsFromFollows

    }

    override suspend fun getPost(postId: String): Post? {
        return posts.findOneById(postId)

    }

    override suspend fun getPostDetails(userId: String, postId: String): PostResponse? {
        val isLiked =
            likes.findOne(Like::userId eq userId) != null //if that is not null then we found the document tha matched the query and ther eis a like

        val post = posts.findOneById(postId) ?: return null
        val user = users.findOneById(post.userId) ?: return null
        return PostResponse(
            id = post.id,
            userId = user.id,
            username = user.username,
            imageUrl = post.imageUrl,
            profilePicture = user.profileImageUrl,
            description = post.description,
            likeCount = post.likeCount,
            commentCount = post.commentCount,
            isLiked = isLiked
        )
    }

    /**
     * We want to return the post which has the user id eq to user id.
     */
    override suspend fun getPostForProfile(ownUserId:String, userId: String, page: Int, pageSize: Int): List<PostResponse> {
        val user = users.findOneById(userId) ?: return emptyList()

        //val users = users.find(User::id eq userId)
        //get the posts for a profile. The user name doesnt really change is just the username of person we loaded the profile from
        return posts.find(Post::userId eq userId)
            .skip(page * pageSize)
            .limit(pageSize) //we onluy want 15 elements at once.
            .descendingSort(Post::timestamp) //order descending because we want that those post made eariel to appear first
            .toList()
            .map { post ->
                //see if a post is liked or not
                val isLiked = likes.findOne(
                    //the parent id of the like needs to be t he post and the other handf tghe userId of the like,. so who liked
                    // the parent id, need to be our user.
                    and(Like::parentId eq post.id,
                    Like::userId eq ownUserId)
                )?.also { println("Found like document: $it") } != null //here we debugg with println
                PostResponse(
                    id = post.id,
                    userId = userId,
                    username = user.username,
                    imageUrl = post.imageUrl,
                    profilePicture = user.profileImageUrl,
                    description = post.description,
                    likeCount = post.likeCount,
                    commentCount = post.commentCount,
                    isLiked = isLiked

                )
            }
        //get all users at once un a local list
        //val users = users.find(User::id `in` posts.map{
//            it.userId
//        })


    }
}



































