package robertconstantin.example.data.repository.activity

import org.litote.kmongo.`in`
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import robertconstantin.example.data.models.Activity
import robertconstantin.example.data.models.Following
import robertconstantin.example.data.models.Post

class ActivityRepositoryImpl(
    db: CoroutineDatabase
): ActivityRepository {

    private val activities = db.getCollection<Activity>()
    //suspend pauses the coroutine as long as the db operation takes place
    override suspend fun getActivitiesForUser(userId: String, page: Int, pageSize: Int): List<Activity> {


        return activities.find(
            //toUserId is like it was the current user.
            Activity::toUserId eq userId
        )
            .skip(page * pageSize)
            .limit(pageSize) //we onluy want 15 elements at once.
            .descendingSort(Activity::timestamp) //order descending because we want that those post made eariel to appear first
            .toList()
    }

    override suspend fun createActivity(activity: Activity) {
        //the arrow means that this block of code will suspend the coroutine till inser finish and then
        //if there is more code then will go on.
        activities.insertOne(activity)
    }

    override suspend fun deleteActivity(activityId: String): Boolean {

        return activities.deleteOneById(activityId).wasAcknowledged()
    }
}