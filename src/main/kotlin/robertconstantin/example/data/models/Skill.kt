package robertconstantin.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import robertconstantin.example.data.responses.SkillDto

data class Skill(
    @BsonId
    val id: String = ObjectId().toString(),
    val name: String,
    val imageUrl: String
){
    fun toSkillResponse(): SkillDto{
        return SkillDto(
            name = name,
            imageUrl = imageUrl
        )
    }
}