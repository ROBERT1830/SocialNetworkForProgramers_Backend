package robertconstantin.example.data.responses

//this is for sending validation messages to client
data class BasicApiResponse<T>(

    val successful: Boolean,
    //because if there is no error no message is needed
    val message: String? = null,
    val data: T? = null

)