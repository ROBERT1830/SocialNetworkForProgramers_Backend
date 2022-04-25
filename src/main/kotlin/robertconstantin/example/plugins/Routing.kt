package robertconstantin.example.plugins

import io.ktor.routing.*
import io.ktor.application.*
import org.koin.ktor.ext.inject
import robertconstantin.example.data.repository.follow.FollowRepository
import robertconstantin.example.data.repository.post.PostRepository
import robertconstantin.example.data.repository.user.UserRepository
import robertconstantin.example.routes.*
import robertconstantin.example.service.FollowService
import robertconstantin.example.service.PostService
import robertconstantin.example.service.UserService

fun Application.configureRouting() {

    //here the koin is called and then will have look where is the dependency, provide it and assign it to
    //that variable here.
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()


    //access the aplication.conf file to get the domain and all the stuf for jwt.
    //Issuer is who create the token. In that case i CREATE IT FROM MY ip and port. Should be a trustable font
    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    routing {
        // User routes
        // for access and make changes to the User documents
        createUserRoute(userService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )

        // Following routes
        followUser(followService)
        unfollowUser(followService)
        //Post routes
        cratePostRoute(postService, userService)
        getPostsForFollows(postService, userService)
    }
}







