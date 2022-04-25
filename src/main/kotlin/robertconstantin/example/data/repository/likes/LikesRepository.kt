package robertconstantin.example.data.repository.likes

interface LikesRepository {

    suspend fun likeParent(userId: String, parentId:String): Boolean //returns if that post was found for that user

    suspend fun unlikeParent(userId: String, parentId:String): Boolean

    suspend fun deleteLikesForParent(parentId: String)
}