package robertconstantin.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Like(

    val userId: String,
    //could be a post or could be a comment
    val parentId: String, //is nice to use it because could be a post or a comment and we dont care
    val parentType: Int,
    @BsonId
    val id: String = ObjectId().toString(),
)
