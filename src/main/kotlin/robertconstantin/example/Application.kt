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
    //The below features from koin depends on koin like routing where we inject the repo.
    //so need to intall koin on top. Else the app throies KoinApp not started
    install(Koin) {
        modules(mainModule)

    }
    configureHTTP()
    configureRouting()
    //configureSockets()
    configureSerialization()
    configureMonitoring()
    configureSecurity()

}
