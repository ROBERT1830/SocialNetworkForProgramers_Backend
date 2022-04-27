package robertconstantin.example.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import robertconstantin.example.plugins.userId


////functions that wuickly verifies the email
//suspend fun PipelineContext<Unit, ApplicationCall>.ifEmailBelongToUser(
//    userId: String,
//    validateEmail: suspend (email: String, userId:String) -> Boolean,
//    onSuccess: suspend () -> Unit
//
//){
//    val isEmailByUser = validateEmail(
//        call.principal<JWTPrincipal>()?.email ?:"", //here is where the extension variable on JWTPrincipal take place.
//        userId
//    )
//    if (isEmailByUser){
//        onSuccess()
//    }else {
//        call.respond(HttpStatusCode.Unauthorized)
//    }
//
//}

// If you take a look, at call you will see that has ApplicationCall. So we can extend that
//and get the userId. You need to know that in each and every request that the client makes
// the token is atatched. So by extending call you can get the clain attatched to the token
//which is user id.
val ApplicationCall.userId: String
    get() = principal<JWTPrincipal>()?.userId.toString()