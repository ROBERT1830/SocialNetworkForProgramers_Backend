package robertconstantin.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Activity(
    @BsonId
    val id: String = ObjectId().toString(),
    val timestamp: Long,
    //who perform an activity on your content
    val byUserId: String,
    //the current user on which the activity was performed
    val toUserId: String,
    val type: Int,
    val parentId: String

)
