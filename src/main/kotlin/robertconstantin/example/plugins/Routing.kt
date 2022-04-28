package robertconstantin.example.plugins

import io.ktor.routing.*
import io.ktor.application.*
import org.koin.ktor.ext.inject
import robertconstantin.example.routes.*
import robertconstantin.example.service.*

fun Application.configureRouting() {

    //here the koin is called and then will have look where is the dependency, provide it and assign it to
    //that variable here.
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()
    val likeService: LikeService by inject()
    val commentService: CommentService by inject()
    val activityService: ActivityService by inject()


    //access the aplication.conf file to get the domain and all the stuf for jwt.
    //Issuer is who create the token. In that case i CREATE IT FROM MY ip and port. Should be a trustable font
    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    routing {
        // User routes
        // for access and make changes to the User documents
        createUser(userService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )
        searchUser(userService)

        // Following routes
        followUser(followService, activityService)
        unfollowUser(followService)
        //Post routes
        cratePostRoute(postService)
        getPostsForFollows(postService)
        deletePost(postService, likeService, commentService)
        //Like routes
        likeParent(likeService, activityService)
        unlikeParent(likeService, userService)
        // Comment routes
        createComments(commentService, activityService)
        deleteComment(commentService, likeService)
        getCommentsForPost(commentService)
        //Activity Routes
        getActivities(activityService)
    }
}







