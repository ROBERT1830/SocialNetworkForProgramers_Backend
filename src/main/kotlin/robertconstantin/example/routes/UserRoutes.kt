package robertconstantin.example.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import robertconstantin.example.controller.user.UserController
import robertconstantin.example.data.models.User
import robertconstantin.example.data.requests.CreateAccountRequest
import robertconstantin.example.data.responses.BasicApiResponse
import robertconstantin.example.util.ApiResponseMessages.FIELDS_BLANK
import robertconstantin.example.util.ApiResponseMessages.USER_ALREADY_EXISTS

fun Route.userRoutes(){

    val userController: UserController by inject()

    //make the path clear.
    route("/api/user/create"){
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

            //Check if the email already exists
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
            val userExists = userController.getUserByEmail(request.email) != null
            if (userExists){
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
            if (request.email.isBlank() || request.password.isBlank() || request.username.isBlank()){
                call.respond(
                    message = BasicApiResponse(
                        message = FIELDS_BLANK,
                        //because the user exists and the login for that user is not succesfull. Needs to choose an other email
                        successful = false
                    )
                )
                return@post
            }

            //after passing he filters, then create a user
            userController.createUser(
                User(
                    email = request.email,
                    username = request.username,
                    password = request.password,
                    profileImageUrl = "",
                    bio = "",
                    githubUrl = null,
                    instagramUrl = null,
                    linkedInUlr = null
                )
            )

            //now after applyy those filters for the data that comes into the server we can respond
            //witha a succesfull message
            call.respond(
                message = BasicApiResponse(
                    successful = true
                //here we dont have message because is succesfull
                )
            )

        }
    }
}
















