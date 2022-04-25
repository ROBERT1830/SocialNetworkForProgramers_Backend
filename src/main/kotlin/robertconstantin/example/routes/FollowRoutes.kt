package robertconstantin.example.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import robertconstantin.example.data.repository.follow.FollowRepository
import robertconstantin.example.data.requests.FollowUpdateRequest
import robertconstantin.example.data.responses.BasicApiResponse
import robertconstantin.example.service.FollowService
import robertconstantin.example.util.ApiResponseMessages.USER_NOT_FOUND

/**
 * That route will be used to follow a user
 */
fun Route.followUser(followService: FollowService){

    route("/api/following/follow"){
        post {
            val request = call.receiveOrNull<FollowUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }


            /**
             * Lets say we wanto to navigate to a user profile and at that time the user
             * that we are currently looking at deletes the account and then we click on follow
             * then te user cand be found. And we sould reply with that. We should respond
             */
            val didUserExist = followService.followUserIfExist(request)
            if (didUserExist){
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse(
                        successful = true
                    )
                )
            }else {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse(
                        successful = false,
                        message = USER_NOT_FOUND
                    )
                )
            }
        }
    }
}

fun Route.unfollowUser(followService: FollowService){

    route("/api/following/unfollow"){
        delete {

            val request = call.receiveOrNull<FollowUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val didUserExist = followService.unFollowUserIfExist(request)
            if (didUserExist){
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse(
                        successful = true
                    )
                )
            }else {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse(
                        successful = false,
                        message = USER_NOT_FOUND
                    )
                )
            }
        }
    }
}










































