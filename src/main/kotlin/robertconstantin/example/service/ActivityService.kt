package robertconstantin.example.service

import robertconstantin.example.data.models.Activity
import robertconstantin.example.data.models.util.ActivityType
import robertconstantin.example.data.models.util.ParentType
import robertconstantin.example.data.repository.activity.ActivityRepository
import robertconstantin.example.data.repository.comment.CommentRepository
import robertconstantin.example.data.repository.post.PostRepository
import robertconstantin.example.data.responses.ActivityResponse
import robertconstantin.example.util.Constants

class ActivityService(
    private val activityRepository: ActivityRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository
) {

    suspend fun getActivitiesForUser(

        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_ACTIVITY_PAGE_SIZE
    ): List<ActivityResponse> {
        return activityRepository.getActivitiesForUser(userId, page, pageSize)
    }

    suspend fun addCommentActivity(
        byUserId: String,
        //commentId: String, //with that we can get the corresponding
        postId: String
    ): Boolean {
        //find the user who owns the post this comments is write below
        //val postIdOfComment = commentRepository.getComment(commentId)?.postId ?: return false
        val userIdOfPost = postRepository.getPost(postId)?.userId ?: return false //get the owner of the post

        //if user who made the comment comment on its comment then no activity table is created.
        if(byUserId == userIdOfPost){
            return false
        }
        activityRepository.createActivity(
            Activity(
                timestamp = System.currentTimeMillis(),
                byUserId = byUserId,
                toUserId = userIdOfPost,
                //No always we have a parent. For example if someone likes our page there is no parent.
                //the parent is "you post" / "your comment"
                type = ActivityType.CommentedOnPost.type,
                parentId = postId
            )
        )
        return true
    }

    suspend fun addLikeActivity(
        byUserId: String,
        parentType: ParentType,
        parentId: String //parent could be a post or a comment. we need to get whoever belong this parent.
    ): Boolean {
        //what makes ens to me is to search in post and comments for the parentId.
        //then get within that post or comment the userId which belongs the comment or post. This one shuld be the
        //toUserId. But this is not scalable.

        val toUserId = when (parentType) {
            is ParentType.Post -> {
                //find the toUserId in post table/document
                postRepository.getPost(parentId)?.userId //get the owner of the comment or post a current user liked.
            }
            is ParentType.Comment -> {
                commentRepository.getComment(parentId)?.userId
            }
            is ParentType.None -> return false
        } ?: return false //if we don't find the userId in either of both commen or post

        //same as above
        if (byUserId == toUserId){
            return false
        }
        activityRepository.createActivity(
            Activity(
                timestamp = System.currentTimeMillis(),
                byUserId = byUserId,
                toUserId = toUserId,
                type = when (parentType) {
                    is ParentType.Post -> ActivityType.LikedPost.type
                    is ParentType.Comment -> ActivityType.LikedComment.type
                    else -> ActivityType.LikedPost.type
                },
                parentId = parentId //indicates in which element the action was performed.
            )
        )
        return true
    }

    suspend fun createActivity(activity: Activity) {
        activityRepository.createActivity(activity)
    }

    suspend fun deleteActivity(activityId: String): Boolean {
        return activityRepository.deleteActivity(activityId)
    }
}