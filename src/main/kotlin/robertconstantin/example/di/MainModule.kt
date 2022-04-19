package robertconstantin.example.di

import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import robertconstantin.example.controller.user.UserController
import robertconstantin.example.controller.user.UserControllerImpl
import robertconstantin.example.util.Constants.DATABASE_NAME


val mainModule = module {
    //Provide the mongoDb
    single {
        //create the mongo client to access db -> need mongo from reactivestreams
        val client = KMongo.createClient().coroutine
        //access db with the mongo client. This is what we want to provide
        //getDatabase return a CoroutineDb. But we wanto to return the superr class i mean
        //thoretically
        /**This provides a CoroutineDb so that is what we will se in the contructor
         * of controller impl*/
        client.getDatabase(DATABASE_NAME)
    }
    //Provide user controller for the repo
    single<UserController> {
        //We use get to provide the db we need here. Because this function what will do
        //is watch what is needed for the controller and the get function get a koin
        //instance of that module to provide it.
        UserControllerImpl(get())
    }
}