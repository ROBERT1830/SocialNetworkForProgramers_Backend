package robertconstantin.example.service

import robertconstantin.example.data.models.Skill
import robertconstantin.example.data.repository.skill.SkillRepository

class SkillService(
    private val repository: SkillRepository
) {
    suspend fun getSkills(): List<Skill>{
        return repository.getSkills()
    }
}