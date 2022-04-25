package robertconstantin.example.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import robertconstantin.example.data.requests.LikeUpdateRequest
import robertconstantin.example.data.responses.BasicApiResponse
import robertconstantin.example.service.LikeService
import robertconstantin.example.service.UserService
import robertconstantin.example.util.ApiResponseMessages

fun Route.likeParent(
    likeService: LikeService,
    userService: UserService
){
    authenticate {
        route("/api/like"){
            post {
                val request = call.receiveOrNull<LikeUpdateRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                //check if the user is really him who will add like. Allways check if the user who makes something is him.
                ifEmailBelongToUser(
                    userId = request.userId,
                    validateEmail = userService::doesEmailBelongToUserId
                ){

                    //create like if the email belong to user that perfom like
                   val likeSuccessful =  likeService.likeParent(request.userId, request.parentId)
                    if (likeSuccessful){
                        call.respond(
                           status =  HttpStatusCode.OK,
                            message = BasicApiResponse(
                                successful = true
                            )
                        )
                    }else{
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = BasicApiResponse(
                                successful = false,
                                message = ApiResponseMessages.USER_NOT_FOUND
                            )

                        )
                    }
                }

            }
        }
    }


}


fun Route.unlikeParent(
    likeService: LikeService,
    userService: UserService
){
    authenticate {
        route("/api/like"){
            delete {
                val request = call.receiveOrNull<LikeUpdateRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                //check if the user is really him who will add like. Allways check if the user who makes something is him.
                ifEmailBelongToUser(
                    userId = request.userId,
                    validateEmail = userService::doesEmailBelongToUserId
                ){

                    //create like if the email belong to user that perfom like
                    val unlikeSuccessful =  likeService.unlikeParent(request.userId, request.parentId)
                    if (unlikeSuccessful){
                        call.respond(
                            status =  HttpStatusCode.OK,
                            message = BasicApiResponse(
                                successful = true
                            )
                        )
                    }else{
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = BasicApiResponse(
                                successful = false,
                                message = ApiResponseMessages.USER_NOT_FOUND
                            )

                        )
                    }
                }

            }
        }
    }


}