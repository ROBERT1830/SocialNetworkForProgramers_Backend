package robertconstantin.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.sql.Timestamp

data class Like(

    val userId: String,
    //could be a post or could be a comment
    val parentId: String, //is nice to use it because could be a post or a comment and we dont care
    val parentType: Int,
    val timestamp: Long,
    @BsonId
    val id: String = ObjectId().toString(),
)
