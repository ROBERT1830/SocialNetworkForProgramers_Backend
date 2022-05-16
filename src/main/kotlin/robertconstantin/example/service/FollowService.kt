package robertconstantin.example.service

import robertconstantin.example.data.repository.follow.FollowRepository
import robertconstantin.example.data.requests.FollowUpdateRequest

class FollowService(
    private val followRepository: FollowRepository
) {

    suspend fun followUserIfExist(request: FollowUpdateRequest, followingUserId: String): Boolean{
        return followRepository.followUserIfExists(
            followingUserId, //me
            request.followedUserId // who I want to follow
        )
    }
    suspend fun unFollowUserIfExist(followedUserId: String, followingUserId: String): Boolean{
        return followRepository.unFollowUserIfExists(
            followingUserId,
            followedUserId
        )
    }

}