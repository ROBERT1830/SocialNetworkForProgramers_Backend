package robertconstantin.example.data.responses

import javax.print.attribute.standard.JobOriginatingUserName

data class ActivityResponse(
    val timestamp: Long,
    val userId: String,
    val parentid: String,
    val type:Int,
    val userName: String,
    //activity id
    val id: String
)
