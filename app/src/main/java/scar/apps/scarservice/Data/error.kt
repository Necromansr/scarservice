package scar.apps.scarservice.Data

import com.google.gson.annotations.SerializedName

data class ErrorMessage(
    @SerializedName("message") val message:String
)