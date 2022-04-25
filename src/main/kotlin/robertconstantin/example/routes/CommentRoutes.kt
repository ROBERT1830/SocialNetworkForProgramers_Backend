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
import robertconstantin.example.service.CommentService
import robertconstantin.example.service.LikeService
import robertconstantin.example.service.UserService
import robertconstantin.example.util.ApiResponseMessages
import robertconstantin.example.util.QueryParams

fun Route.createComments(
    commentService: CommentService,
    userService: UserService
){
    authenticate {
        route("api/comment/create"){
            post {
                val request = call.receiveOrNull<CreateCommentRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                ifEmailBelongToUser(
                    userId = request.userId,
                    validateEmail = userService::doesEmailBelongToUserId
                ){
                    when(commentService.createComment(request)){
                        is CommentService.ValidationEvent.ErrorFieldEmpty -> {
                            call.respond(
                                HttpStatusCode.OK,
                                BasicApiResponse(
                                    successful = false,
                                    message = ApiResponseMessages.FIELDS_BLANK
                                )
                            )
                        }
                        is CommentService.ValidationEvent.ErrorCommentTooLong -> {
                            call.respond(
                                HttpStatusCode.OK,
                                BasicApiResponse(
                                    successful = false,
                                    message = ApiResponseMessages.COMMENT_TOO_LONG
                                )
                            )

                        }
                        is CommentService.ValidationEvent.Success -> {
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
    userService: UserService,
    likeService: LikeService
){
    authenticate {
        delete("api/comment/delete") {
            val request = call.receiveOrNull<DeleteCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            ifEmailBelongToUser(
                userId = request.userId,
                validateEmail = userService::doesEmailBelongToUserId

            ){
                //delete comment
                val deleted = commentService.deleteComment(request.commentId)
                if (deleted) {
                    // ????????? why when delete a comment deletes a like.
                    likeService.deleteLikesForParent(request.commentId)
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = BasicApiResponse(successful = true)
                    )
                }else{
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = BasicApiResponse(successful = false)
                    )
                }
            }
        }
    }

}














































