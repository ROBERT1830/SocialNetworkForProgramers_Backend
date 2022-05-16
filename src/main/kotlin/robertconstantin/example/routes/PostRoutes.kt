package robertconstantin.example.routes

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import robertconstantin.example.data.requests.CreatePostRequest
import robertconstantin.example.data.requests.DeletePostRequest
import robertconstantin.example.data.responses.BasicApiResponse
import robertconstantin.example.service.CommentService
import robertconstantin.example.service.LikeService
import robertconstantin.example.service.PostService
import robertconstantin.example.service.UserService
import robertconstantin.example.util.ApiResponseMessages.USER_NOT_FOUND
import robertconstantin.example.util.Constants
import robertconstantin.example.util.Constants.DEFAULT_POST_PAGE_SIZE
import robertconstantin.example.util.QueryParams
import robertconstantin.example.util.QueryParams.PARAM_PAGE
import robertconstantin.example.util.QueryParams.PARAM_PAGE_SIZE
import robertconstantin.example.util.save
import java.io.File
import java.util.*


fun Route.cratePostRoute(
    postService: PostService
){
    val gson by inject<Gson>()

    /*We have to authenticate first before doing the create post task. */
    /*This authenticaye will only let pass in request that actually have that valid token attatched.
    * Whenever a client now makes a requestto an authenticated route, the validate block
    * will fire of */
    authenticate {
        route("/api/post/create"){
            post {
//                val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
//                    call.respond(HttpStatusCode.BadRequest)
//                    return@post
//                }

//                //remember that in the withClain we actually attatch the unique email.
//                /*So the yser logs in and the user email is saved in the token whoch the user cant modifiy
//                * and here we get that email from that token*/
////                val email = call.principal<JWTPrincipal>()?.getClaim("email", String::class) //email is a string
////                //before create the post we want to verify that wmail of user made request of this route
////                //uses its own email
////                val isEmailByUser = userService.doesEmailBelongToUserId(
////                    //the email is that one that is attatched in our token. Because that is something
////                    //that the user cant modify
////                    email = email?: "",
////                    userId = request.userId
////                //check if the email is equel of the user email that want to create  the post for
////                )
////
////                if (!isEmailByUser){
////                    call.respond(
////                        status = HttpStatusCode.Unauthorized,
////                        message = "Your are not who you say you are."
////                    )
////                    return@post
////                }
//
//                //Above code could be writen like this using our own extension funciton.
//
//                /**
//                 * If a user makes an authenticated request, then we can directly determine
//                 * who make that request by using the userId.
//                 * So the app doesnt pass that userId explicitely anymore. Instead we just determine it from the
//                 * token.
//                 */
//                val userId = call.userId //userId contained in the token
//
//
//                val didUserExists = postService.createPost(request, userId)
//
//                if (!didUserExists){
//                    call.respond(
//                        HttpStatusCode.OK,
//                        BasicApiResponse(
//                            successful = false,
//                            message = USER_NOT_FOUND
//                        )
//                    )
//                } else{
//                    call.respond(
//                        HttpStatusCode.OK,
//                        BasicApiResponse(
//                            successful = true,
//
//                            )
//                    )
//                }
//
////                ifEmailBelongToUser(
////                    userId = request.userId,
////                    validateEmail = userService::doesEmailBelongToUserId
////                ){
////
////                }


                // -----------------------------------------------

                /**
                 * is not necesary to check if the user exists or not because we have its id in the token
                 * and we know that he amkes a request is him and exists.
                 */
//                val didUserExists = postService.createPost(request, userId)
//
//                if (!didUserExists){
//                    call.respond(
//                        HttpStatusCode.OK,
//                        BasicApiResponse(
//                            successful = false,
//                            message = USER_NOT_FOUND
//                        )
//                    )
//                    return@post
//                }
                //receive the multipar if the user extists
                val multipart = call.receiveMultipart()
                var fileName: String? = null //contains the profile image
                var createPostRequest: CreatePostRequest? = null
                multipart.forEachPart { partData ->
                    when (partData) {
                        is PartData.FormItem -> {
                            //"update_profile_data" used to detect the attatched json string of the multipart request.
                            if (partData.name == "post_data") {
                                createPostRequest =
                                    gson.fromJson<CreatePostRequest>(
                                        partData.value,
                                        CreatePostRequest::class.java
                                    )
                            }

                        }
                        is PartData.FileItem -> {
                            /**
                             * Here we can create an extension function because we have duplicated code in update profile
                             * because is the same code. So we can create an extension function in FileItem
                             */
                            fileName = partData.save(Constants.POST_PICTURE_PATH)

                        }
                        is PartData.BinaryItem -> Unit
                    }
                }

                //The file is uploaded
                val postPictureUrl = "${Constants.BASE_URL}post_pictures/$fileName" //can be reached because is static, we specified in routing that resources can be reached directly from staic folder.
                /**
                 * when createPostRequest is not null. that menas that we attatched te request (with the JSON) and we saved an image in
                 * the file syste,
                 */
                createPostRequest?.let { request ->

                    val createPostAcknowledge = postService.createPost(
                        request = request,
                        userId = call.userId,
                        imageUrl = postPictureUrl
                    )
                    if (createPostAcknowledge){
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = BasicApiResponse<Unit>(
                                successful = true
                            )
                        )
                    }else{
                        //If the update of the profile was not succesful for some reason we want to delete
                        //the file we created with the image.
                        File("${Constants.POST_PICTURE_PATH}/$fileName").delete()
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                } ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post

                }



            }
        }
    }

}


fun Route.getPostsForProfile(
    postService: PostService
) {
    authenticate {
        get("/api/user/posts") {
            //pass the userId from where we want to get the posts. Because mayby should be some else profile.
            //But when we want to se our own profile, then we won't attatch an id. So will be used ours.
            val userId = call.parameters[QueryParams.USER_ID]
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull()
                ?: 0 //if null get first page. convert to int the parameter from the query
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE
            val posts = postService.getPostForProfile(
                userId = userId ?: call.userId,
                page = page,
                pageSize = pageSize
            )
            call.respond(
                status = HttpStatusCode.OK,
                message = posts
            )
        }
    }
}

fun Route.getPostsForFollows(
    postService: PostService
){
    authenticate {
        //Because is a get request we have query parameters and not json body like in post.
        get("api/post/get") {
            //userId could be null and if it is null, we respond with bad request
//            val userId = call.parameters[PARAM_USER_ID] ?: kotlin.run {
//                call.respond(HttpStatusCode.BadRequest)
//                return@get
//            }
            val page = call.parameters[PARAM_PAGE]?.toIntOrNull() ?: 0 //if null get first page. convert to int the parameter from the query
            val pageSize = call.parameters[PARAM_PAGE_SIZE]?.toIntOrNull()?:DEFAULT_POST_PAGE_SIZE

            //call.userId --> get it from token
            val posts = postService.getPostForFollows(call.userId, page, pageSize)
            call.respond(
                status = HttpStatusCode.OK,
                message = posts
            )

            //validate that the user is actually who they tell they are.
            //extension function on PipelineContext
            //only the person who actually follows people can their post.
//            ifEmailBelongToUser(
//                userId = userId,
//                validateEmail = { //for shortcut --> validateEmial = userService::doesEmailBelongToUserId and automatically both parameters will be passed.
//                    email: String, userId: String ->
//                    userService.doesEmailBelongToUserId(email = email, userId = userId)
//                }
//            ){
//                //if the email belongs to user we want to retrieve te posts
//
//
//            }

        }
    }
}




fun Route.deletePost(
    postService: PostService,
    likeService: LikeService,
    commentService: CommentService
){
    authenticate {
        //here we have a delete post reqeust. So we need a new request model which will be get from client side
        route("/api/post/delete"){
            delete {
                val request = call.receiveOrNull<DeletePostRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                //Now we want to get the post and check if that actually belongs

                val post = postService.getPost(request.postId)

                if (post == null){
                    call.respond(
                        message = HttpStatusCode.NotFound
                    )
                    return@delete
                }
                //the post contains the userId who made the post. So if the userId that made the post, is equal to
                //the token id then allow to delete.
                if (post.userId == call.userId){
                    postService.deletePost(request.postId)
                    likeService.deleteLikesForParent(request.postId)
                    // TODO: 25/4/22 delete comments from post
                    commentService.deleteCommentsForPost(request.postId)
                    call.respond(HttpStatusCode.OK)
                }else{
                    call.respond( HttpStatusCode.Unauthorized)
                }



                //if the post collection was found check if the

//            ifEmailBelongToUser(
//                //check if the post of the user belongs to him.
//                userId = post.userId,
//                validateEmail = userService::doesEmailBelongToUserId
//            ){
//                //Now in that lambda if it executes means that the user is him and is allowed to delete his own post
//
//            }
            }
        }
    }


}















































