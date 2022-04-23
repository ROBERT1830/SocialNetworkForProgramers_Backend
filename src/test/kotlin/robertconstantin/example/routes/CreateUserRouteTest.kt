package robertconstantin.example.routes


import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import robertconstantin.example.data.models.User
import robertconstantin.example.data.requests.CreateAccountRequest
import robertconstantin.example.data.responses.BasicApiResponse
import robertconstantin.example.di.testModule
import robertconstantin.example.plugins.configureSerialization
import robertconstantin.example.repository.user.FakeUserRepository
import robertconstantin.example.util.ApiResponseMessages.FIELDS_BLANK
import robertconstantin.example.util.ApiResponseMessages.USER_ALREADY_EXISTS
import kotlin.test.BeforeTest

/**
 * We wanto to be able to access the repositry within taht use case. So for inject the fake repo
 * then is neded to extend KointTest class.
 *
 * THEN nedd a test module that provide a the testRepository fake
 */
internal class CreateUserRouteTest : KoinTest {

    //inject the fake repo
    private val userRepository by inject<FakeUserRepository>()
    private val gson = Gson()


    //this function is called before  every single test case So the peaceof code here is not needed to
    //ve placed in every single etst case. So for all our tesst cases here we want to star the koin injection
    //to have access to the repo
    @BeforeTest
    fun setUp() {
        startKoin {
            modules(testModule)
        }
    }






    //START TESTING

    //1--If we don't attach a body then a bad request will be sent. That is how we made it.
    //here we specify that with this test case will test the create user option with no body attached
    //and with a expect response of BadRequest.

    @Test
    fun `Create user, no body attached, responds with BadRequest`(){
        //in module function we can place the plugis we wanto to test. In this case because we test the route
        //then we need that plugin and we need to provide the routing we wanto to test so that we
        //make sure thata the test case will have it.
        withTestApplication(moduleFunction = {
            install(Routing) {
                createUserRoute(userRepository)
            }
        }) {
            //when performing a test for post, you will need to declare the method as post and the uri
            //from where this post take place.
            //SIMULATE THE REQUEST
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "api/user/create"
            ) /**Here we are not appening the curly braces to put some kind of object to have a body. SO we are asserting no boddy attatched*/
            //in this specifc case we dont have a body attatched (because is an error badrequest because no body attatch).
            // We wanto to make sure that server respond with bad repond
            assertThat(request.response.status()).isEqualTo(HttpStatusCode.BadRequest)
            

        }
    }
    
    /**Test if the user enters an email that exists to repond woth USER_ALREADY_EXISTS and succesfull false 
     * from our BasicApiResponse*/
    @Test
    fun `Create user, user already exists, responds with unsuccessful`() = runBlocking{
        //For this test is need to have a user inside it. To check if we introduce the same
        //the reponse will be unnsuccesfull.So is like we add to the mutable list a user to simulate that
        //we have some one inside the db. For taht user the repo functions and its functions to add to the list
        //because we are inserting into the db the functions are suspending. So call them from runBlocking
        val user = User(
            email = "test@test.com",
            username = "test",
            password = "test",
            profileImageUrl = "",
            bio = "",
            githubUrl = null,
            instagramUrl = null,
            linkedInUlr = null

        )
        userRepository.createUser(user)
        //So now we make sure that we have a user in the db
        //Now we make a reqeust to create the same user here and check if the reponse is unsuccesfull.
        /**Need a GSON dependency because is what we use to serialize and deserialize*/

        withTestApplication(moduleFunction = {
            install(Routing) {
                //for parsing the object
                configureSerialization()
                createUserRoute(userRepository)
            }
        }) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "api/user/create"
            ){
                addHeader("Content-Type", "application/json")
                //type request to post. This is the type that our server will get
                //this is the object that is send to the server
                //1-Define the object that is gona bee accepted
                val request = CreateAccountRequest(
                    email = "test@test.com",
                    username = "asas",
                    password = "asas"
                )
                //serialize the object into JSON because the server will receive that object serialized from the client
                //2-serailize the object. Is like we are making the same steps that takes place when clinet send object to server
                //whith this we have the object serialized
                setBody(gson.toJson(request))
            }
            //3-Now the server wil lsend a repons for that post. So you have to deserialize it and send it to the client
            //that is what happens in real life and what we are trying to simulate here
            val response = gson.fromJson<BasicApiResponse>(
                //here we get the reposnse and its contentt from server and is what the server will send
                request.response.content ?: "",
                //type of class thta the response is made from.
                BasicApiResponse::class.java

            )
            //the response will we false for succesful
            assertThat(response.successful).isFalse()
            //assert the type of message which is to the one we specified in the server resposne. Use equal to because is string
            assertThat(response.message).isEqualTo(USER_ALREADY_EXISTS)


        }
    }


    @Test
    fun `Create user, email is empty, responds with unsuccessful`() = runBlocking{
        withTestApplication(moduleFunction = {
            install(Routing) {
                configureSerialization()
                createUserRoute(userRepository)
            }
        }) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "api/user/create"
            ){
                addHeader("Content-Type", "application/json")
                val request = CreateAccountRequest(
                    email = "",
                    username = "",
                    password = ""
                )
                //set the body of the request. perform serialization to get in the server
                setBody(gson.toJson(request))
            }
            val response = gson.fromJson<BasicApiResponse>(
                request.response.content ?: "",
                BasicApiResponse::class.java
            )
            assertThat(response.successful).isFalse()
            assertThat(response.message).isEqualTo(FIELDS_BLANK)


        }
    }

    /**
     * What happens here for checking that we create a user in the database is the fgollowing.
     * the first thing that happen is that will go to the UserRoutes. But take into account
     * that here in the install routing we are passing the fake repository version which will have
     * the same method but working on a mutable list as a databse.
     * So when running the test, will go to UserRoutes.kt file passing the fake repository and will check
     * if there is a user or not in the databse hich is simulated here by a mutable list in our
     * fake repository. Of course there is no user. So will pass to check the other filter
     * and all them apss and finally create a user with the data contained in the object we are passing
     * (val request =CreateAccountRequest ....).
     * After that, the code path reached the runBlocking code and checks if there is an user inside the list
     * and it is a new user created. So its asserts to true.
     */
    @Test
    fun `Create user,valid data, responds with successful`(){
        withTestApplication(moduleFunction = {
            install(Routing) {
                configureSerialization()
                createUserRoute(userRepository)
            }
        }) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "api/user/create"
            ){
                addHeader("Content-Type", "application/json")
                val request = CreateAccountRequest(
                    email = "test@test.com",
                    username = "test",
                    password = "test"
                )
                setBody(gson.toJson(request))
            }
            val response = gson.fromJson<BasicApiResponse>(
                request.response.content ?: "",
                BasicApiResponse::class.java

            )
            assertThat(response.successful).isTrue()

            //assert that user is inserted in db because all is ok
            runBlocking {
                val isUserInDb = userRepository.getUserByEmail("test@test.com") != null
                assertThat(isUserInDb).isTrue()
            }




        }
    }
}






























