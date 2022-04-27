package robertconstantin.example.data.repository.likes

import robertconstantin.example.data.models.util.ParentType

interface LikesRepository {

    suspend fun likeParent(userId: String, parentId:String, parentType: Int): Boolean //returns if that post was found for that user

    suspend fun unlikeParent(userId: String, parentId:String): Boolean

    suspend fun deleteLikesForParent(parentId: String)
}