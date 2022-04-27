package robertconstantin.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Like(

    val userId: String,
    //could be a post or could be a comment
    val parentId: String,
    @BsonId
    val id: String = ObjectId().toString(),
)
