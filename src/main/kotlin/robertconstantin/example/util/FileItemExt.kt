package robertconstantin.example.util

import io.ktor.http.content.*
import java.io.File
import java.nio.file.Paths
import java.util.*

//this function will return the file name
fun PartData.FileItem.save(path: String): String{

    //create a stream to the file and read its bytes
    //this is part data
    val fileBytes = this.streamProvider().readBytes()
    //get the original file extension after dot. For example if you upload image.png the extension will be png
    val fileExtension = this.originalFileName?.takeLastWhile {
        it != '.'
    }
    //random file name with the original file extension
    val fileName = UUID.randomUUID().toString() + "." + fileExtension
    val folder = File(path)
    //create a file in whih we write the bites of the image. the path of the file should be in src/main and are palved in resources/static/profile_pictures

    folder.mkdirs()
    File("$path$fileName").writeBytes(fileBytes)
    return fileName
}