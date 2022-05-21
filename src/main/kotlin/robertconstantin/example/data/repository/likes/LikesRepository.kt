package robertconstantin.example.data.repository.likes

import robertconstantin.example.data.models.Like
import robertconstantin.example.data.models.util.ParentType
import robertconstantin.example.util.Constants

interface LikesRepository {

    suspend fun likeParent(userId: String, parentId:String, parentType: Int): Boolean //returns if that post was found for that user

    suspend fun unlikeParent(userId: String, parentId:String, parentType: Int): Boolean

    suspend fun deleteLikesForParent(parentId: String)

    //we need to map th Like ot the userResponseItem in the service.
    suspend fun getLikesForParent(
        parentId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_ACTIVITY_PAGE_SIZE
    ): List<Like>
}