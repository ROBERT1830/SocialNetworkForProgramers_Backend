package robertconstantin.example.plugins

import io.ktor.routing.*
import io.ktor.application.*
import org.koin.ktor.ext.inject
import robertconstantin.example.data.repository.follow.FollowRepository
import robertconstantin.example.data.repository.user.UserRepository
import robertconstantin.example.routes.createUserRoute
import robertconstantin.example.routes.followUser
import robertconstantin.example.routes.loginUser
import robertconstantin.example.routes.unfollowUser

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val followRepository: FollowRepository by inject()
    routing {
        // User routes
        // for access and make changes to the User documents
        createUserRoute(userRepository)
        loginUser(userRepository)

        // Following routes
        followUser(followRepository)
        unfollowUser(followRepository)
    }
}
