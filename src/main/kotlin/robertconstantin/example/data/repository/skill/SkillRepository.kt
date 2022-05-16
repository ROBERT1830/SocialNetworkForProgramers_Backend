package robertconstantin.example.data.repository.skill

import robertconstantin.example.data.models.Skill

interface SkillRepository {

    suspend fun getSkills(): List<Skill>
}