package scar.apps.scarservice.Data
import com.google.gson.annotations.SerializedName
data class User(
    @SerializedName("Name") val Name:String,
    @SerializedName("Store") val Store:String,
    @SerializedName("Position") val Position:String,
    @SerializedName("Token") val Token:String,
    @SerializedName("description") val description:String
)
data class Login(val Username:String,val Password:String)

class UserDto {
    var name: String = ""
    var comment: String = ""

    constructor() {}

    constructor(name: String, comment: String) {
        this.name = name
        this.comment = comment
    }
}