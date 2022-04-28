package robertconstantin.example.di

import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import robertconstantin.example.data.repository.activity.ActivityRepository
import robertconstantin.example.data.repository.activity.ActivityRepositoryImpl
import robertconstantin.example.data.repository.comment.CommentRepository
import robertconstantin.example.data.repository.comment.CommentRepositoryImpl
import robertconstantin.example.data.repository.follow.FollowRepository
import robertconstantin.example.data.repository.follow.FollowRepositoryImpl
import robertconstantin.example.data.repository.likes.LikesRepository
import robertconstantin.example.data.repository.likes.LikesRepositoryImpl
import robertconstantin.example.data.repository.post.PostRepository
import robertconstantin.example.data.repository.post.PostRepositoryImpl
import robertconstantin.example.data.repository.user.UserRepository
import robertconstantin.example.data.repository.user.UserRepositoryImpl
import robertconstantin.example.service.*
import robertconstantin.example.util.Constants.DATABASE_NAME


val mainModule = module {
    //Provide the mongoDb
    single {
        //create the mongo client to access db -> need mongo from reactivestreams
        val client = KMongo.createClient().coroutine
        //access db with the mongo client. This is what we want to provide
        //getDatabase return a CoroutineDb.
        /**This provides a CoroutineDb so that is what we will se in the contructor
         * of controller impl*/
        client.getDatabase(DATABASE_NAME)
    }
    //Provide user controller for the repo
    /**Cada vez que accedas al repo impl con la itnerfaz entonces te va a decir koin vale tenog el modulo aqui y te lo paso*/
    single<UserRepository> {
        //We use get to provide the db we need here. Because this function what will do
        //is watch what is needed for the controller and the get function get a koin
        //instance of that module to provide it.
        UserRepositoryImpl(get())
    }
    single<FollowRepository> {
        FollowRepositoryImpl(get())
    }

    single<PostRepository> {
        PostRepositoryImpl(get())
    }

    single<LikesRepository> { LikesRepositoryImpl(get()) }

    single<CommentRepository> { CommentRepositoryImpl(get()) }

    single<ActivityRepository> { ActivityRepositoryImpl(get()) }

    /********PROVIDE THE USER SERVICE********/

    single {
        UserService(get(), get())
    }
    single { FollowService(get()) }

    single { PostService(get()) }

    single { LikeService(get()) }

    single {CommentService(get())}

    single {
        ActivityService(get(), get(), get()) //one get for each repo. koin will se what data is needed and then will come here to provide it.
    }
}