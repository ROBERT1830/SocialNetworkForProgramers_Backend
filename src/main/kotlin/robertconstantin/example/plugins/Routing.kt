package robertconstantin.example.plugins

import io.ktor.routing.*
import io.ktor.application.*
import org.koin.ktor.ext.inject
import robertconstantin.example.repository.user.UserRepository
import robertconstantin.example.routes.createUserRoute

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    routing {
        //routes for access and make changes to the User documents
        createUserRoute(userRepository)
    }
}
