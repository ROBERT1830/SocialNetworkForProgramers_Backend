package robertconstantin.example.service

import robertconstantin.example.data.repository.follow.FollowRepository
import robertconstantin.example.data.repository.likes.LikesRepository
import robertconstantin.example.data.repository.user.UserRepository
import robertconstantin.example.data.responses.UserResponseItem

class LikeService(
    private val likeRespository: LikesRepository,
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {
    suspend fun likeParent(userId: String, parentId: String, parentType: Int): Boolean {
        return likeRespository.likeParent(userId, parentId, parentType)
    }

    suspend fun unlikeParent(userId: String, parentId: String): Boolean {
        return likeRespository.unlikeParent(userId, parentId)
    }

    suspend fun deleteLikesForParent(parentId: String){
        likeRespository.deleteLikesForParent(parentId)
    }

    //who likes a given comment or a post
    suspend fun getUsersWhoLikedParent(parentId: String, userId: String): List<UserResponseItem>{
        //get the likes for a single parend and then get all userId of those likes
        val userIds = likeRespository.getLikesForParent(parentId).map {
            it.userId //map to a list of users that we want to display.
        }
        //get all users that are in those likes antries from db. so likes the post
        val users = userRepository.getUsers(userIds)
        //get the people taht follow current user
        val followsByUser = followRepository.getFollowsbyUser(userId)
        return users.map { user ->
            val isFollowing = followsByUser.find { it.followedUserId == user.id } != null
            UserResponseItem(
                userId = user.id,
                userName = user.username,
                profilePictureUrl = user.profileImageUrl,
                bio = user.bio,
                isFollowing = isFollowing
            )
        }
    }





}