package robertconstantin.example.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import robertconstantin.example.data.requests.CreateCommentRequest
import robertconstantin.example.data.requests.DeleteCommentRequest
import robertconstantin.example.data.requests.DeletePostRequest
import robertconstantin.example.data.responses.BasicApiResponse
import robertconstantin.example.service.ActivityService
import robertconstantin.example.service.CommentService
import robertconstantin.example.service.LikeService
import robertconstantin.example.service.UserService
import robertconstantin.example.util.ApiResponseMessages
import robertconstantin.example.util.QueryParams

fun Route.createComments(
    commentService: CommentService,
    activityService: ActivityService
){
    authenticate {
        route("api/comment/create"){
            post {
                val request = call.receiveOrNull<CreateCommentRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val userId = call.userId
                when(commentService.createComment(request, userId)){
                    is CommentService.ValidationEvent.ErrorFieldEmpty -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(
                                successful = false,
                                message = ApiResponseMessages.FIELDS_BLANK
                            )
                        )
                    }
                    is CommentService.ValidationEvent.ErrorCommentTooLong -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(
                                successful = false,
                                message = ApiResponseMessages.COMMENT_TOO_LONG
                            )
                        )

                    }
                    is CommentService.ValidationEvent.Success -> {
                        activityService.addCommentActivity(
                            byUserId = userId,
                            postId = request.postId, //this is the parent
                            //commentId = result.commentId
                        )
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(
                                successful = true,
                            )
                        )

                    }
                }
//                ifEmailBelongToUser(
//                    userId = request.userId,
//                    validateEmail = userService::doesEmailBelongToUserId
//                ){
//
//                }
            }
        }
    }
}

fun Route.getCommentsForPost(
    commentService: CommentService,
){
    authenticate {
        get("api/comment/get") {
            val postId = call.parameters[QueryParams.PARAM_POST_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }


            val comments = commentService.getCommentsForPost(postId)
            call.respond(HttpStatusCode.OK, comments)
        }
    }
}

fun Route.deleteComment(
    commentService: CommentService,
    likeService: LikeService
){
    authenticate {
        delete("api/comment/delete") {
            val request = call.receiveOrNull<DeleteCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            /**
             * When delete a comment we want to make sure that the user actually owwns the comment
             * for that check the comment user id with the user id from the token. So first get the
             * comment that has userId equals to token id which is current user that performs the call so terver
             */

            val comment = commentService.getCommentById(request.commentId)
            if (comment?.userId != call.userId){
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }

            //delete comment
            val deleted = commentService.deleteComment(request.commentId)
            if (deleted) {
                //delete likes for a specific comment. parent here is the comment
                likeService.deleteLikesForParent(request.commentId)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse<Unit>(successful = true)
                )
            }else{
                call.respond(
                    status = HttpStatusCode.NotFound,
                    message = BasicApiResponse<Unit>(successful = false)
                )
            }
//            ifEmailBelongToUser(
//                userId = request.userId,
//                validateEmail = userService::doesEmailBelongToUserId
//            ){
//
//            }
        }
    }

}














































