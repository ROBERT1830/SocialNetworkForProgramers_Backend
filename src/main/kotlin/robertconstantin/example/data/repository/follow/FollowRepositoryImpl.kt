package robertconstantin.example.data.repository.follow

import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import robertconstantin.example.data.models.Following
import robertconstantin.example.data.models.User

class FollowRepositoryImpl(
    db: CoroutineDatabase
): FollowRepository {

    private val following = db.getCollection<Following>()
    //to check if the user that we want to follow exists.
    private val users = db.getCollection<User>()


    /**
     * This function should just call our databse and add a simple document with this id
     * So we should check somewhere else
     */
    override suspend fun followUserIfExists(

        //the person i want to follow
        followingUserId: String,
        //me as follower
        followedUserId: String
    ): Boolean {
        /*First we need to check if both users exists before making a follow*/
        //we need to check the user that we wanto to follow. But the   question is should we do it
        //here or not, So is there a case
        val doesFollowingUserExist = users.findOneById(followingUserId) != null
        val doesFollowedUserExist = users.findOneById(followedUserId) != null
        if (!doesFollowedUserExist || !doesFollowingUserExist){
            return false
        }

        following.insertOne(
            Following(followingUserId, followedUserId)
        )
        return true

        /*We could have thought to have in the user document 2 variables with following and
        * followed. But imagine that a user have 1 million followers. then you have a list inside
        * of each user document that you retrieve that has a 1 million ids. Thre is no way to paginate that
        * . So because that we use an other collection for that.
        * Also if you want to remove a following id from a list that is actually and entry of a document
        * then you need to update the total list. You need to replace the whole list. */

    }

    override suspend fun unFollowUserIfExists(
        followingUserId: String,
        followedUserId: String):Boolean {

        /*We unfollow a user if we find a collection Following
        * in which the following (el qeu yo sigo) and the followed (me because i follow him)
        * are equal to the id we pass. iN THAT CASE THE COLLECTION wil be removed.
        * deleteOne return a DeleteResult*/
       val deleteResult = following.deleteOne(
           //delete if there is a collection in which the followinid and followeuserid matches
            and(
                Following::followingUserId eq followingUserId,
                Following::followedUserId eq followedUserId
            )
        )
        //deleteResult return a  DeleteResult from which we can get the deleteCount.
        //if that is greater thatn 0 means that we deleted a Following collection that contains
        //the id of the user we want to unfollow and our id.
        //the deletion entry will only be 1 because the id are unique.
        return deleteResult.deletedCount > 0


    }
}




























