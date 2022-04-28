package robertconstantin.example.data.requests

/**
 * we will upload the picture toguether with this reqeust
 */
data class UpdateProfileRequest(
    //we allways update the whole profile. The data will be filled by default but if we update a name
    //we wil save that instead. if not save again the default value
    val userName: String,
    val bio: String,
    val githubUrl: String,
    val instagramUrl: String,
    val linkedInUrl: String,
    val skills: List<String>,
    val profileImageChanged: Boolean = false

)