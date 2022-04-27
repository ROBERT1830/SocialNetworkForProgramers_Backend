package robertconstantin.example.data.repository.activity

import robertconstantin.example.data.models.Activity
import robertconstantin.example.util.Constants.DEFAULT_ACTIVITY_PAGE_SIZE

interface ActivityRepository {

    suspend fun getActivitiesForUser(
        userId: String,
        page: Int = 0,
        pageSize: Int = DEFAULT_ACTIVITY_PAGE_SIZE): List<Activity>

    suspend fun createActivity(activity: Activity)

    suspend fun deleteActivity(activityId: String): Boolean
}