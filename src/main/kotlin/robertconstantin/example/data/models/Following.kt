package robertconstantin.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Following(

    //who i follows
    val followingUserId: String,
    //who follows me
    val followedUserId:String,
    @BsonId
    val id: String = ObjectId().toString(),

)
