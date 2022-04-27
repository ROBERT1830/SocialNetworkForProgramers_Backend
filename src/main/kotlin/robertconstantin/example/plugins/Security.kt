package robertconstantin.example.plugins

import io.ktor.auth.*
import io.ktor.util.*
import io.ktor.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureSecurity() {

    authentication {
        jwt {

            val jwtAudience = environment.config.property("jwt.audience").getString()
            realm = environment.config.property("jwt.realm").getString()
            verifier(
                JWT
                    .require(Algorithm.HMAC256("secret"))
                    .withAudience(jwtAudience)
                    .withIssuer(environment.config.property("jwt.domain").getString())
                    .build()
            )
            // credential -> contains JWTCredential. So that will then contain the data of taht token. For example the payload
            //we can attatch any kind of data to that. For example what we want to attatch is the email or user id
            //bevause we want to validade server side if the email of the token is actually the email of the person who
            //made the request
            validate { credential ->
                //if we are allowed to access the route of auth. Then return a principle. Principle in
                //ktor is an object of an authenticated user.
                if (credential.payload.audience.contains(jwtAudience)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }

}


//extension variable to get the id and not vrite the function over and over again with the claim.


val JWTPrincipal.userId: String?
    get() = getClaim("userId", String::class)