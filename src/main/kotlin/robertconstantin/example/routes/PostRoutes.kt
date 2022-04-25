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
import robertconstantin.example.service.PostService
import robertconstantin.example.service.UserService
import robertconstantin.example.util.ApiResponseMessages.USER_NOT_FOUND

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
                val email = call.principal<JWTPrincipal>()?.getClaim("email", String::class) //email is a string
                //before create the post we want to verify that wmail of user made request of this route
                //uses its own email
                val isEmailByUser = userService.doesEmailBelongToUserId(
                    //the email is that one that is attatched in our token. Because that is something
                    //that the user cant modify
                    email = email?: "",
                    userId = request.userId
                //check if the email is equel of the user email that want to create  the post for
                )

                if (!isEmailByUser){
                    call.respond(
                        status = HttpStatusCode.Unauthorized,
                        message = "Your are not who you say you are."
                    )
                    return@post
                }

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