package scar.apps.scarservice.Data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Products(
    @SerializedName("Id") val Id:String,
    @SerializedName("Name") val Name:String,
    @SerializedName("Description") val Description:String,
    @SerializedName("Sku") val Sku:String,
    @SerializedName("Barcode") val Barcode:String,
    @SerializedName("Maker") val Maker:String,
    @SerializedName("WeightSharp") val WeightSharp:Double,
    @SerializedName("WeightAprox") val WeightAprox:Double,
    @SerializedName("Place") val Place:String,
    @SerializedName("InStock") val InStock:String,
    var Places:String = ""
):Serializable
