package robertconstantin.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import robertconstantin.example.data.responses.ProfileResponse

data class User(

    val email: String,
    val username: String,
    val password: String,
    val profileImageUrl: String,
    val bio: String,
    val githubUrl: String?,
    val instagramUrl: String?,
    val linkedInUlr: String?,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val postCount: Int = 0,
    //is default parameter and should not became before not default ones
    val skills: List<String> = listOf(),
    //in mongo id is a string. By this annotation the mongo will create an id for you
    @BsonId
    val id: String = ObjectId().toString(),


)
