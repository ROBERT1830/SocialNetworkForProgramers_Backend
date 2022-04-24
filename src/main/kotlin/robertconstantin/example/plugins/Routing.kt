package robertconstantin.example.plugins

import io.ktor.routing.*
import io.ktor.application.*
import org.koin.ktor.ext.inject
import robertconstantin.example.data.repository.follow.FollowRepository
import robertconstantin.example.data.repository.post.PostRepository
import robertconstantin.example.data.repository.user.UserRepository
import robertconstantin.example.routes.*

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val followRepository: FollowRepository by inject()
    val postRepository: PostRepository by inject()
    routing {
        // User routes
        // for access and make changes to the User documents
        createUserRoute(userRepository)
        loginUser(userRepository)

        // Following routes
        followUser(followRepository)
        unfollowUser(followRepository)
        //Post routes
        cratePostRoute(postRepository)
    }
}
