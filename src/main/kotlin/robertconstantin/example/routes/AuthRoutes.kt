package robertconstantin.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import robertconstantin.example.data.requests.CreateAccountRequest
import robertconstantin.example.data.requests.LoginRequest
import robertconstantin.example.data.responses.AuthResponse
import robertconstantin.example.data.responses.BasicApiResponse
import robertconstantin.example.service.UserService
import robertconstantin.example.util.ApiResponseMessages
import java.util.*


fun Route.createUser(userService: UserService) {

    /**Is not needed to be authenticate when create a user. only when login and performin task inside the app*/

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
                    message = BasicApiResponse<Unit>(
                        message = ApiResponseMessages.USER_ALREADY_EXISTS,
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
                        message = BasicApiResponse<Unit>(
                            message = ApiResponseMessages.FIELDS_BLANK,
                            successful = false
                        )
                    )
                }
                is UserService.ValidationEvent.SuccessEvent -> {
                    //now after applyy those filters for the data that comes into the server we can respond
                    //witha a succesfull message
                    userService.createUser(request)
                    call.respond(
                        //Unit because the sealed class wont cointn extra data of a type String for example for yhe token
                        message = BasicApiResponse<Unit>(
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
                    message = BasicApiResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessages.INVALID_CREDENTIALS
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
                    message = BasicApiResponse(
                        successful = true,
                        data = AuthResponse(
                            token = token
                        )
                    )
                )
            } else {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessages.INVALID_CREDENTIALS
                    )
                )
            }

        }
    }
}

/**
 * Check if the token has expired
 *
 * In case someone attatches an invalid token or no token at all this will repond with
 * unauthorized by default so that ktor will do that for us. If repons with ok
 * we know the reuest goies through and then we authenticated.
 */
fun Route.authenticate(){
    authenticate {
        get("api/user/authenticate") {
            //When we get the response here we know that we are succesfully authenticated
            call.respond(HttpStatusCode.OK)
            //if ht token expired then will tell the user to login again and will show the login screen again

        }
    }
}




























