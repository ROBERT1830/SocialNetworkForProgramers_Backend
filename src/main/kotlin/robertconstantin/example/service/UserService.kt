package robertconstantin.example.service

import io.ktor.application.*
import io.ktor.response.*
import robertconstantin.example.data.models.User
import robertconstantin.example.data.repository.user.UserRepository
import robertconstantin.example.data.requests.CreateAccountRequest
import robertconstantin.example.data.requests.LoginRequest
import robertconstantin.example.data.responses.BasicApiResponse
import robertconstantin.example.util.ApiResponseMessages

class UserService(
    private val repository: UserRepository
) {
    suspend fun doesUserWithEmailExist(email: String): Boolean{
        return repository.getUserByEmail(email) != null
    }

    suspend fun doesEmailBelongToUserId(email: String, userId:String): Boolean{
        return repository.doesEmailBelongToUserId(email, userId)
    }

    fun validateCreateAccountRequest(request: CreateAccountRequest): ValidationEvent{

        //Cheack if the request is empty or not
        if (request.email.isBlank() || request.password.isBlank() || request.username.isBlank()){
           return ValidationEvent.ErrorFieldEmpty
        }
        return ValidationEvent.SuccessEvent
    }

    suspend fun doesPasswordMatchForUser(request: LoginRequest): Boolean{
       return repository.doesPasswordForUserMatch(
            email = request.email,
            enteredPassword = request.password
        )
    }

    suspend fun createUser(request: CreateAccountRequest){
        repository.createUser(
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
    }

    /********************/
    sealed class ValidationEvent {
        object ErrorFieldEmpty: ValidationEvent()
        object SuccessEvent: ValidationEvent()
    }
}