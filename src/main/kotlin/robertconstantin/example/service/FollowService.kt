package robertconstantin.example.service

import robertconstantin.example.data.repository.follow.FollowRepository
import robertconstantin.example.data.requests.FollowUpdateRequest

class FollowService(
    private val followRepository: FollowRepository
) {

    suspend fun followUserIfExist(request: FollowUpdateRequest): Boolean{
        return followRepository.followUserIfExists(
            request.followingUserId, //me
            request.followedUserId // who I want to follow
        )
    }
    suspend fun unFollowUserIfExist(request: FollowUpdateRequest): Boolean{
        return followRepository.unFollowUserIfExists(
            request.followingUserId,
            request.followedUserId
        )
    }

}