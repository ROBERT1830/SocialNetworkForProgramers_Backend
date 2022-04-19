package robertconstantin.example

import io.ktor.application.*
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.modules
import robertconstantin.example.di.mainModule
import robertconstantin.example.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureHTTP()
    configureRouting()
    //configureSockets()
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    install(Koin) {
        modules(mainModule)

    }
}
