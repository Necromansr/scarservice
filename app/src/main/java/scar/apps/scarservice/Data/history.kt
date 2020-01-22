package scar.apps.scarservice.Data

import com.google.gson.annotations.SerializedName

data class History(
    @SerializedName("Id") val Id:String,
    @SerializedName("Description") val Description:String,
    @SerializedName("Sku") val Sku:String,
    @SerializedName("Barcode") val Barcode:String,
    @SerializedName("Maker") val Maker:String,
    @SerializedName("Name") val Name:String
    )

data class historyResult(
    @SerializedName("Result") val Result:List<History>
)