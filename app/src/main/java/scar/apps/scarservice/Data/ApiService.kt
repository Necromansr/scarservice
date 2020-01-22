package scar.apps.scarservice.Data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    @GET("/api/v1.0/Products/search/{barcode}")
    fun search(@Header("Authorization") token:String,@Path("barcode") barcode:String): Call<List<History>>
    @GET("/api/v1.0/Places/{barcode}")
    fun place(@Header("Authorization") token:String,@Path("barcode") barcode:String): Call<List<String>>
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/v1.0/users/login")
    fun login(@Body body: Login): Call<User>

    @Headers(
        "Content-Type: application/json"
    )
    @GET("api/v1.0/Products/history")
    fun history(@Header("Authorization") token:String): Call<List<History>>


    @GET("api/v1.0/Products/{id}")
    fun product(@Header("Authorization") token:String,@Path("id") id:String): Call<Products>
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/v1.0/Products/print/{id}")
    fun print(@Header("Authorization") token:String,@Path("id") id:String): Call<ErrorMessage>
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/v1.0/Products/print/{id}/{number}")
    fun prints(@Header("Authorization") token:String,@Path("id") id:String,@Path("number") number:String): Call<ErrorMessage>
    @Headers(
        "Content-Type: application/json"
    )
    @PUT("/api/v1.0/Products/weight/{id}/{weight}")
    fun weight(@Header("Authorization") token:String,@Path("id") id:String,@Path("weight") weight:Double): Call<Products>
    @Headers(
        "Content-Type: application/json"
    )
    @PUT("/api/v1.0/Products/place/{id}/{place}")
    fun place(@Header("Authorization") token:String,@Path("id") id:String,@Path("place") place:String): Call<Products>

    @GET("/api/v1.0/Documents/{barcode}")
    fun document(@Header("Authorization") token:String,@Path("barcode") barcode:String): Call<Documents>
    @Headers(
        "Content-Type: application/json"
    )
    @PUT("api/v1.0/Documents")
    fun documents(@Header("Authorization") token:String,@Body body: List<String>): Call<ErrorMessage>

    @Headers(
        "Content-Type: application/json"
    )
    @PUT("/api/v1.0/Products/places/{place}")
    fun Place(@Header("Authorization") token:String,@Body body: List<String>,@Path("place") place:String): Call<ErrorMessage>

    companion object Factory{
        fun create(): ApiService {
            val gson: Gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("https://store.scar.ua/").build()
            return retrofit.create(ApiService::class.java)
        }
    }
}