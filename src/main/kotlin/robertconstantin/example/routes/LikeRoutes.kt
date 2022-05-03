package robertconstantin.example.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import robertconstantin.example.data.models.util.ParentType
import robertconstantin.example.data.requests.LikeUpdateRequest
import robertconstantin.example.data.responses.BasicApiResponse
import robertconstantin.example.service.ActivityService
import robertconstantin.example.service.LikeService
import robertconstantin.example.service.UserService
import robertconstantin.example.util.ApiResponseMessages
import robertconstantin.example.util.QueryParams.PARAM_PARENT_ID

fun Route.likeParent(
    likeService: LikeService,
    activityService: ActivityService
){
    authenticate {
        route("/api/like"){
            post {
                val request = call.receiveOrNull<LikeUpdateRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val userId = call.userId

                //create like if the email belong to user that perfom like
                //HERE PARENT ID COULD BE A COMMENT OR A POST (because we can perform likes on both)
                val likeSuccessful =  likeService.likeParent(userId, request.parentId, request.parentType)
                if (likeSuccessful){
                    //create an activity
                    activityService.addLikeActivity(
                        byUserId = userId,
                        parentType = ParentType.fromType(request.parentType),
                        parentId = request.parentId
                    )
                    call.respond(
                        status =  HttpStatusCode.OK,
                        message = BasicApiResponse<Unit>(
                            successful = true
                        )
                    )
                }else{
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = BasicApiResponse<Unit>(
                            successful = false,
                            message = ApiResponseMessages.USER_NOT_FOUND
                        )

                    )
                }

//                //check if the user is really him who will add like. Allways check if the user who makes something is him.
//                ifEmailBelongToUser(
//                    userId = request.userId,
//                    validateEmail = userService::doesEmailBelongToUserId
//                ){
//
//
//                }

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

                //create like if the email belong to user that perfom like
                val unlikeSuccessful =  likeService.unlikeParent(call.userId, request.parentId)
                if (unlikeSuccessful){
                    call.respond(
                        status =  HttpStatusCode.OK,
                        message = BasicApiResponse<Unit>(
                            successful = true
                        )
                    )
                }else{
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = BasicApiResponse<Unit>(
                            successful = false,
                            message = ApiResponseMessages.USER_NOT_FOUND
                        )

                    )
                }

                /*//check if the user is really him who will add like. Allways check if the user who makes something is him.
                ifEmailBelongToUser(
                    userId = request.userId,
                    validateEmail = userService::doesEmailBelongToUserId
                ){


                }*/

            }
        }
    }

}


fun Route.getLikesForParent(likesService: LikeService){
    authenticate {
        get("api/like/parent") {

            val parentId = call.parameters[PARAM_PARENT_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val usersWhoLikedParent = likesService.getUsersWhoLikedParent(
                parentId = parentId,
                call.userId
            )

            call.respond(
                HttpStatusCode.OK,
                usersWhoLikedParent
            )


        }
    }
}




































