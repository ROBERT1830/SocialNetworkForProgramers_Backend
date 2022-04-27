package robertconstantin.example.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import robertconstantin.example.service.ActivityService
import robertconstantin.example.service.PostService
import robertconstantin.example.util.Constants
import robertconstantin.example.util.QueryParams

fun Route.getActivities(
    activityService: ActivityService
){
    authenticate {
        //Because is a get request we have query parameters and not json body like in post.
        get("api/activity/get") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0 //if null get first page. convert to int the parameter from the query
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull()?: Constants.DEFAULT_POST_PAGE_SIZE

            //call.userId --> get it from token
            val activities = activityService.getActivitiesForUser(call.userId, page, pageSize)
            call.respond(
                status = HttpStatusCode.OK,
                message = activities
            )


        }
    }
}

/**
 * We dont have a route to create activities. Instead we want to create an activity when an action is performed.
 * So when somebody like the post for example. An activity will be created.
 * So in like route we need to do something. That is the place to create an activity documnet.
 */