package robertconstantin.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Post(

    val imageUrl: String,
    val userId: String,
    val timestamp: Long,
    val description: String,
    //both of them are used to display related post number.
    val likeCount: Int = 0,
    val commentCount : Int = 0,
    @BsonId
    val id: String = ObjectId().toString(),
)
