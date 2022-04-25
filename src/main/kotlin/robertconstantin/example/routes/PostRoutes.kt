package robertconstantin.example.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import robertconstantin.example.data.models.Post
import robertconstantin.example.data.repository.post.PostRepository
import robertconstantin.example.data.requests.CreatePostRequest
import robertconstantin.example.data.requests.FollowUpdateRequest
import robertconstantin.example.data.responses.BasicApiResponse
import robertconstantin.example.plugins.email
import robertconstantin.example.service.PostService
import robertconstantin.example.service.UserService
import robertconstantin.example.util.ApiResponseMessages.USER_NOT_FOUND
import robertconstantin.example.util.Constants.DEFAULT_POST_PAGE_SIZE
import robertconstantin.example.util.QueryParams.PARAM_PAGE
import robertconstantin.example.util.QueryParams.PARAM_PAGE_SIZE
import robertconstantin.example.util.QueryParams.PARAM_USER_ID

fun Route.cratePostRoute(
    postService: PostService,
    userService: UserService
){

    /*We have to authenticate first before doing the create post task. */
    /*This authenticaye will only let pass in request that actually have that valid token attatched.
    * Whenever a client now makes a requestto an authenticated route, the validate block
    * will fire of */
    authenticate {
        route("/api/post/create"){
            post {
                val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                //remember that in the withClain we actually attatch the unique email.
                /*So the yser logs in and the user email is saved in the token whoch the user cant modifiy
                * and here we get that email from that token*/
//                val email = call.principal<JWTPrincipal>()?.getClaim("email", String::class) //email is a string
//                //before create the post we want to verify that wmail of user made request of this route
//                //uses its own email
//                val isEmailByUser = userService.doesEmailBelongToUserId(
//                    //the email is that one that is attatched in our token. Because that is something
//                    //that the user cant modify
//                    email = email?: "",
//                    userId = request.userId
//                //check if the email is equel of the user email that want to create  the post for
//                )
//
//                if (!isEmailByUser){
//                    call.respond(
//                        status = HttpStatusCode.Unauthorized,
//                        message = "Your are not who you say you are."
//                    )
//                    return@post
//                }

                //Above code could be writen like this using our own extension funciton.
                ifEmailBelongToUser(
                    userId = request.userId,
                    validateEmail = userService::doesEmailBelongToUserId
                ){
                    val didUserExists = postService.createPostIfUserExists(request)

                    if (!didUserExists){
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = false,
                                message = USER_NOT_FOUND
                            )
                        )
                    } else{
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = true,

                                )
                        )
                    }
                }





            }
        }
    }

}

fun Route.getPostsForFollows(
    postService: PostService,
    userService: UserService
){
    authenticate {
        //Because is a get request we have query parameters and not json body like in post.
        get {
            //userId could be null and if it is null, we respond with bad request
            val userId = call.parameters[PARAM_USER_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val page = call.parameters[PARAM_PAGE]?.toIntOrNull() ?: 0 //if null get first page. convert to int the parameter from the query
            val pageSize = call.parameters[PARAM_PAGE_SIZE]?.toIntOrNull()?:DEFAULT_POST_PAGE_SIZE

            //validate that the user is actually who they tell they are.
            //extension function on PipelineContext
            ifEmailBelongToUser(
                userId = userId,
                validateEmail = { //for shortcut --> validateEmial = userService::doesEmailBelongToUserId and automatically both parameters will be passed.
                    email: String, userId: String ->
                    userService.doesEmailBelongToUserId(email = email, userId = userId)
                }
            ){
                //if the email belongs to user we want to retrieve te posts
                val posts = postService.getPostForFollows(userId, page, pageSize)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = posts
                )

            }

        }
    }
}
















































