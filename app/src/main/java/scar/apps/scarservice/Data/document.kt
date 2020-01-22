package scar.apps.scarservice.Data

import com.google.gson.annotations.SerializedName

data class Documents(
    @SerializedName("Name") val Name:String,
    @SerializedName("Barcode") val Barcode:String
)

