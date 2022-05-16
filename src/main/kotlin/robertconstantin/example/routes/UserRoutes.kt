package robertconstantin.example.routes

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import robertconstantin.example.data.models.User
import robertconstantin.example.data.requests.UpdateProfileRequest
import robertconstantin.example.data.responses.BasicApiResponse
import robertconstantin.example.data.responses.UserResponseItem
import robertconstantin.example.service.PostService
import robertconstantin.example.service.UserService
import robertconstantin.example.util.ApiResponseMessages.USER_NOT_FOUND
import robertconstantin.example.util.Constants
import robertconstantin.example.util.Constants.BANNER_IMAGE_PATH
import robertconstantin.example.util.Constants.PROFILE_PICTURE_PATH
import robertconstantin.example.util.QueryParams
import robertconstantin.example.util.QueryParams.PARAM_QUERY
import robertconstantin.example.util.QueryParams.USER_ID
import robertconstantin.example.util.save
import java.io.File


/**
 * When the user search the info that will be displayed will be
 * the image profile, user name, bio, and if we already follwoing or not
 */
fun Route.searchUser(userService: UserService) {
    authenticate {
        get("/api/user/search") {
            val query = call.parameters[PARAM_QUERY]
            if (query == null || query.isBlank()) {
                call.respond(
                    status = HttpStatusCode.OK,
                    listOf<UserResponseItem>()
                )
                return@get
            }
            val searchResults = userService.searchForUsers(query, call.userId)
            // TODO: 28/4/22 check if list of userResponseItem is empty. Means that current user doesn't follow somebody that matches that query.
            call.respond(
                status = HttpStatusCode.OK,
                message = searchResults
            )
        }
    }
}


fun Route.getUserProfile(userService: UserService) {
    authenticate {
        get("/api/user/profile") {
            val userId = call.parameters[USER_ID]
            //if we didn't attach that parameter or is empty return empty list
            if (userId == null || userId.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                )
                return@get
            }
            //userId --> could be the current or other user if tab in there
            //call.userId --> current user id who made the request
            val profileResponse = userService.getUserProfile(userId, call.userId)

            if (profileResponse == null) {
                call.respond(
                    HttpStatusCode.OK,
                    message = BasicApiResponse<Unit>(
                        successful = false,
                        message = USER_NOT_FOUND
                    )
                )
                return@get
            }
            call.respond(
                status = HttpStatusCode.OK,
                message = BasicApiResponse(
                    successful = true,
                    data = profileResponse
                )
            )
        }
    }
}




fun Route.updateUserProfile(userService: UserService) {
    val gson: Gson by inject()
    authenticate {
        put("/api/user/update") {
//            val userId = call.parameters[USER_ID]
//            //if we didn't attach that parameter or is empty return empty list
//            if (userId == null || userId.isBlank()){
//                call.respond(
//                    HttpStatusCode.BadRequest,
//                )
//                return@put
//            }


//            val request = call.receiveOrNull<UpdateProfileRequest>() ?: kotlin.run {
//                call.respond(HttpStatusCode.BadRequest)
//                return@put
//            }

            /**
             * We need to get an image. We get those images using multipart request
             * multipart request also contains byte data in form of a file. Its is helpfull to
             * get an image.
             *
             * We receive the iamge with receiveMultipart and then loop through this parts.
             * we only want to upload a image if the profile image  change
             *
             * FormItem --> will be for example the JSON string.
             * FleItem --> will be the image.
             *
             * we need to kind of request client side. One with and one without file (contains image)
             *
             * --> Because here we will work with an iamge we cant get the object as it is
             * we first need a multipart request. From that parse the JSON. partData contains the JSON
             */

            //multipart data in that case receives a json and a file with an image. .
            val multipart = call.receiveMultipart()
            var profilePictureFileName: String? = null //contains the profile image
            var bannerImageFileName: String? = null
            var updateProfileRequest: UpdateProfileRequest? = null
            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        //"update_profile_data" used to detect the attatched json string of the multipart request.
                        if (partData.name == "update_profile_data") {
                            updateProfileRequest =
                                gson.fromJson<UpdateProfileRequest>(
                                    partData.value,
                                    UpdateProfileRequest::class.java
                                )
                        }

                    }
                    is PartData.FileItem -> {
//                        println(Paths.get("").toAbsolutePath().toString())
//                        //create a stream to the file and read its bytes
//                        val fileBytes = partData.streamProvider().readBytes()
//                        //get the original file extension after dot. For example if you upload image.png the extension will be png
//                        val fileExtension = partData.originalFileName?.takeLastWhile {
//                            it != '.'
//                        }
//                        //random file name with the original file extension
//                        fileName = UUID.randomUUID().toString() + "." + fileExtension
//                        //create a file in whih we write the bites of the image. the path of the file should be in src/main and are palved in resources/static/profile_pictures
//                        File("$PROFILE_PICTURE_PATH$fileName").writeBytes(fileBytes)

                        /**
                         * Useing of an extension function for a cleaner code.
                         */
                        if (partData.name == "profile_picture") {
                            profilePictureFileName = partData.save(PROFILE_PICTURE_PATH)
                        } else if (partData.name == "banner_image") {
                            profilePictureFileName = partData.save(BANNER_IMAGE_PATH)
                        }


                    }
                    is PartData.BinaryItem -> Unit
                }
            }

            //The file is uploaded
            val profilePictureUrl = "${Constants.BASE_URL}profile_pictures/$profilePictureFileName"
            val bannerImageUrl = "${Constants.BASE_URL}banner_images/$bannerImageFileName"
            updateProfileRequest?.let {
                //now update the user entry in the db
                val updateAcknowledge = userService.updateUser(
                    userId = call.userId,
                    profileImageUrl = if (profilePictureFileName == null) {
                        null
                    } else {
                        profilePictureUrl
                    },
                    bannerUrl = if (bannerImageFileName == null) {
                        null
                    } else {
                        bannerImageUrl
                    },
                    updateProfileRequest = it
                )
                if (updateAcknowledge) {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = BasicApiResponse<Unit>(
                            successful = true
                        )
                    )
                } else {
                    //If the update of the profile was not succesful for some reason we want to delete
                    //the file we created with the image.
                    File("${Constants.PROFILE_PICTURE_PATH}/$profilePictureFileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put

            }


        }
    }
}











































