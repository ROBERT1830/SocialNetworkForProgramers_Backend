package robertconstantin.example.data.repository.skill

import org.litote.kmongo.coroutine.CoroutineDatabase
import robertconstantin.example.data.models.Skill

class SkillRepositoryImpl(
    private val db: CoroutineDatabase
): SkillRepository {

    private val skills = db.getCollection<Skill>()



    override suspend fun getSkills(): List<Skill> {
        //insert one skill manually for testing

//        Skill(
//            name = "Kotlin",
//            imageUrl = "http://10.0.2.2:8001/skills\n" +
//                    "/ic_kotlin.svg"
//        )

        //find() ---> get all documents in the collection
        return skills.find().toList()

    }
}