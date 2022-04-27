package robertconstantin.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Activity(

    val timestamp: Long,
    //who perform an activity on your content. Is the current user who is doing the action of like for example
    val byUserId: String,
    //the user on which the activity was performed
    val toUserId: String,
    val type: Int,
    val parentId: String,
    @BsonId
    val id: String = ObjectId().toString()

)