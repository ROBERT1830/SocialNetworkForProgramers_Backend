package robertconstantin.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.inject
import robertconstantin.example.data.models.User
import robertconstantin.example.data.requests.CreateAccountRequest
import robertconstantin.example.data.requests.LoginRequest
import robertconstantin.example.data.requests.UpdateProfileRequest
import robertconstantin.example.data.responses.AuthResponse
import robertconstantin.example.data.responses.BasicApiResponse
import robertconstantin.example.service.PostService
import robertconstantin.example.service.UserService
import robertconstantin.example.util.ApiResponseMessages.FIELDS_BLANK
import robertconstantin.example.util.ApiResponseMessages.INVALID_CREDENTIALS
import robertconstantin.example.util.ApiResponseMessages.USER_ALREADY_EXISTS
import robertconstantin.example.util.ApiResponseMessages.USER_NOT_FOUND
import robertconstantin.example.util.Constants
import robertconstantin.example.util.Constants.PROFILE_PICTURE_PATH
import robertconstantin.example.util.QueryParams
import robertconstantin.example.util.QueryParams.PARAM_QUERY
import robertconstantin.example.util.QueryParams.USER_ID
import java.io.File
import java.nio.file.Paths
import java.util.*

fun Route.createUser(userService: UserService) {


    //make the path clear.
    route("/api/user/create") {
        //make a post request to make an account
        post {
            //pass the data class type from to receive. data class de la que recibes
            //receiveOrNull --> in case we receive some invalid JSON data from the client
            //just return null.
            /**So think what we get from the user when they click on the register button?
             * Is the email, username and password. So we need a dataclass that contains
             * all of taht info
             * Ther server will receive the CreateAccountRequest with the registration data
             * for introducing a new user in db
             *
             * --> What I think that actually will do is when user press button to register, we
             * create an object of this data in the client and then serialize it and send it to the
             * server by using a post request with the path specified. Then whith the code below
             * we are telling that the server will receive that serialized object from client
             * which is of type of CreateAccountRequest.
             * */
            val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            /**ONCE WE GET THE DATA FROM CLIENT AUTOMATICALLY THE PLUGIN OF SERIALIZATION WILL
             * DESERIALIZE THE JSON TO THE OBJECT WE WANT TO WORK WITH. NOW LET TAKE THE OBJECT
             * AND BEFORE CREATING A USER PERFORM SOME CHECKS.**/
            //Check if the email already exists in mongodb
            /**What will happen is that from the client side the server will receive a
             * JSON from a dataclass because the data to be sended need to be serialized
             * Then when the info get in the server it will be deserialized and here we are
             * telling that the server will receive an data class object with the corresponding
             * data from JSON. So now we need to check the db that the user with that email
             * exists. For that we will user the controller which has access to the db.
             * And which collection are we loking for? This is defined in the contorller
             * once we get inyected the db. And is the following
             *  private val users = db.getCollection<User>(). So now with getUserByEmail
             *  we can perform a query on that document with the filter that is composed
             *  by the type of document we wanto to chek and its specific value and equeal to
             *  certain parameter. This Finds the first document (user) that match the filter in the collection (list of users).
             *  check if it is not null in one line*/

            if (userService.doesUserWithEmailExist(request.email)) {
                //if the user exists then we will send a ApiResponse. So we need to create that api response
                //Here if we pass a message as a data class, because we have the content negotiation feature with GSON
                //it will automatically parse the data class to JSON when we say call.respond
                call.respond(
                    message = BasicApiResponse(
                        message = USER_ALREADY_EXISTS,
                        //because the user exists and the login for that user is not succesfull. Needs to choose an other email
                        successful = false
                    )
                )
                return@post
            }

            //Cheack if the request is empty or not
            when (userService.validateCreateAccountRequest(request)) {
                is UserService.ValidationEvent.ErrorFieldEmpty -> {
                    call.respond(
                        message = BasicApiResponse(
                            message = FIELDS_BLANK,
                            successful = false
                        )
                    )
                }
                is UserService.ValidationEvent.SuccessEvent -> {
                    //now after applyy those filters for the data that comes into the server we can respond
                    //witha a succesfull message
                    userService.createUser(request)
                    call.respond(
                        message = BasicApiResponse(
                            successful = true
                            //here we dont have message because is succesfull
                        )
                    )

                }
            }
        }
    }
}

fun Route.loginUser(
    userService: UserService,
    jwtIssuer: String,
    jwtAudience: String,
    jwtSecret: String
) {


    route("/api/user/login") {
        post {
            //when the request arrives the server will be seriealized and automatically the plugin will
            //deserailize it to wor with that object.
            val request = call.receiveOrNull<LoginRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if (request.email.isBlank() && request.password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            //2 ways for auth. We use sealed clases for that.
            //The thing is that if the user logs iin with name we have to find the name of the user
            //in the db.and if they used email we have to find the email in the db.
//            val authMethod = if (request.email.isNotBlank()){
//                AuthMethod.Email
//            }else AuthMethod.Username
            /**-->We decide to use only the email and not the name to register because
             * if that occurs the name sould be unique and could be many name like our in the app.*/

            //get reference of the user that is login in
            //if there is no user to retrive with that email, just respond with invalid.
            val user = userService.getUserByEmail(request.email) ?: kotlin.run {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse(
                        successful = false,
                        message = INVALID_CREDENTIALS
                    )
                )
                return@post
            }
            val isCorrectPassword = userService.isValidPassword(
                enteredPassword = request.password,
                //is whatever is saved in the db for that user
                actualPassword = user.password
            )
            if (isCorrectPassword) {
                //that is milliseconds, min, hour, days, number of days.
                val expiresIn = 1000L * 60L * 60L * 24L * 365L

                //if passsword is correct then create a token that will be responded by server and get
                //by the app. The app will use that token to perfom the request after the user loggs in.
                /*Attatch some data to the token withClain. We want to attatch the email (i sunique) of the user
                * who currentlu loggs from the request.
                * You create a token with the following data, the email because is unique (so that we will know
                * if the user is him when performin something like create a post or delete it),
                * the issuer which is the domain from where is created, exipre data and audience
                *
                * So now in each and every reqeust from cleint that token will be attatched.
                * This token will be checked before perform path stuff like create a post.  */
                val token = JWT.create()
                    .withClaim("userId", user.id)
                    .withIssuer(jwtIssuer)
                    //define expire date for token
                    .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
                    .withAudience(jwtAudience)
                    .sign(Algorithm.HMAC256(jwtSecret))

                call.respond(
                    status = HttpStatusCode.OK,
                    message = AuthResponse(
                        token = token
                    )
                )
            } else {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse(
                        successful = false,
                        message = INVALID_CREDENTIALS
                    )
                )
            }

        }
    }
}

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
                    listOf<User>()
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
                    message = BasicApiResponse(
                        successful = false,
                        message = USER_NOT_FOUND
                    )
                )
                return@get
            }
            call.respond(
                status = HttpStatusCode.OK,
                message = profileResponse
            )
        }
    }
}

fun Route.getPostsForProfile(
    postService: PostService
) {
    authenticate {
        get("api/user/posts") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull()
                ?: 0 //if null get first page. convert to int the parameter from the query
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE
            val posts = postService.getPostForProfile(
                userId = call.userId,
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
            var fileName: String? = null //contains the profile image
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
                        println(Paths.get("").toAbsolutePath().toString())
                        //create a stream to the file and read its bytes
                        val fileBytes = partData.streamProvider().readBytes()
                        //get the original file extension after dot. For example if you upload image.png the extension will be png
                        val fileExtension = partData.originalFileName?.takeLastWhile {
                            it != '.'
                        }
                        //random file name with the original file extension
                        fileName = UUID.randomUUID().toString() + "." + fileExtension
                        //create a file in whih we write the bites of the image. the path of the file should be in src/main and are palved in resources/static/profile_pictures
                        File("$PROFILE_PICTURE_PATH$fileName").writeBytes(fileBytes)
                    }
                    is PartData.BinaryItem -> Unit
                }
            }

            //The file is uploaded
            val profilePictureUrl = "${Constants.BASE_URL}profile_pictures/$fileName"
            updateProfileRequest?.let {
                //now update the user entry in the db
                val updateAcknowledge = userService.updateUser(
                    userId = call.userId,
                    profileImageUrl = profilePictureUrl,
                    updateProfileRequest = it
                )
                if (updateAcknowledge){
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = BasicApiResponse(
                            successful = true
                        )
                    )
                }else{
                    //If the update of the profile was not succesful for some reason we want to delete
                    //the file we created with the image.
                    File("${Constants.PROFILE_PICTURE_PATH}/$fileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put

            }


        }
    }
}











































