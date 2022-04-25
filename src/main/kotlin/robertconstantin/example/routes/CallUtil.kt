package robertconstantin.example.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import robertconstantin.example.plugins.email

//functions that wuickly verifies the email
suspend fun PipelineContext<Unit, ApplicationCall>.ifEmailBelongToUser(
    userId: String,
    validateEmail: suspend (email: String, userId:String) -> Boolean,
    onSuccess: suspend () -> Unit

){
    val isEmailByUser = validateEmail(
        call.principal<JWTPrincipal>()?.email ?:"", //here is where the extension variable on JWTPrincipal take place.
        userId
    )
    if (isEmailByUser){
        onSuccess()
    }else {
        call.respond(HttpStatusCode.Unauthorized)
    }

}