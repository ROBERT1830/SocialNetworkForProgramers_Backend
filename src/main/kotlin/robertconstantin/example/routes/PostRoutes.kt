package robertconstantin.example.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import robertconstantin.example.data.models.Post
import robertconstantin.example.data.repository.post.PostRepository
import robertconstantin.example.data.requests.CreatePostRequest
import robertconstantin.example.data.requests.FollowUpdateRequest
import robertconstantin.example.data.responses.BasicApiResponse
import robertconstantin.example.util.ApiResponseMessages.USER_NOT_FOUND

fun Route.cratePostRoute(postRepository: PostRepository){
    route("/api/post/create"){
        post {
            val request = call.receiveOrNull<CreatePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val didUserExists = postRepository.createPostIfUserExists(
                Post(
                    imageUrl = "",
                    userId = request.userId,
                    timestamp = System.currentTimeMillis(),
                    description = request.description
                )
            )
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