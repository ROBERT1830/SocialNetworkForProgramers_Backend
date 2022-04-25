package robertconstantin.example.plugins

import io.ktor.routing.*
import io.ktor.application.*
import org.koin.ktor.ext.inject
import robertconstantin.example.data.repository.follow.FollowRepository
import robertconstantin.example.data.repository.post.PostRepository
import robertconstantin.example.data.repository.user.UserRepository
import robertconstantin.example.routes.*
import robertconstantin.example.service.FollowService
import robertconstantin.example.service.UserService

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val userService: UserService by inject()
    val followRepository: FollowRepository by inject()
    val followService: FollowService by inject()
    val postRepository: PostRepository by inject()


    routing {
        // User routes
        // for access and make changes to the User documents
        createUserRoute(userService)
        loginUser(userRepository)

        // Following routes
        followUser(followService)
        unfollowUser(followService)
        //Post routes
        cratePostRoute(postRepository)
    }
}
