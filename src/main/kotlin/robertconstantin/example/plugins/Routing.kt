package robertconstantin.example.plugins

import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.http.content.*
import org.koin.ktor.ext.inject
import robertconstantin.example.routes.*
import robertconstantin.example.service.*
import java.io.File

fun Application.configureRouting() {

    //here the koin is called and then will have look where is the dependency, provide it and assign it to
    //that variable here.
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()
    val likeService: LikeService by inject()
    val commentService: CommentService by inject()
    val activityService: ActivityService by inject()
    val skillService: SkillService by inject()


    //access the aplication.conf file to get the domain and all the stuf for jwt.
    //Issuer is who create the token. In that case i CREATE IT FROM MY ip and port. Should be a trustable font
    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    routing {
        // User routes
        authenticate()
        // for access and make changes to the User documents
        createUser(userService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )
        searchUser(userService)
        getUserProfile(userService)
        getPostsForProfile(postService)
        updateUserProfile(userService)

        // Following routes
        followUser(followService, activityService)
        unfollowUser(followService)
        //Post routes
        cratePostRoute(postService)
        getPostsForFollows(postService)
        deletePost(postService, likeService, commentService)
        getPostDetails(postService)
        //Like routes
        likeParent(likeService, activityService)
        unlikeParent(likeService, userService)
        getLikesForParent(likeService)
        // Comment routes
        createComments(commentService, activityService)
        deleteComment(commentService, likeService)
        getCommentsForPost(commentService)
        //Activity Routes
        getActivities(activityService)

        // Skill routes
        getSkills(skillService = skillService )


        //this will provide the files. With this files we will have the profile picture url
//        static() {
//            staticRootFolder = File("./static")
//            //set the root folder for static files
//            files("static")
//        }

        static {
            resources("static")
        }
    }
}







