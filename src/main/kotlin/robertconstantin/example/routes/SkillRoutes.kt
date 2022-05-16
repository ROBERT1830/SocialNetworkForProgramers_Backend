package robertconstantin.example.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import robertconstantin.example.service.SkillService

fun Route.getSkills(skillService: SkillService){
    authenticate {
        get("/api/skills/get"){
            call.respond(
                status = HttpStatusCode.OK,
                skillService.getSkills().map {
                    it.toSkillResponse()
                }
            )
        }
    }
}