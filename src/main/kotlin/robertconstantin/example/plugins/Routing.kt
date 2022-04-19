package robertconstantin.example.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import robertconstantin.example.routes.userRoutes

fun Application.configureRouting() {
    routing {
        //routes for access and make changes to the User documents
        userRoutes()
    }
}
