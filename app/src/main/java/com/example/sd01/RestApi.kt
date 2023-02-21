package com.example.sd01


import com.google.gson.annotations.SerializedName
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface RestApi {
    @Headers(
        "Content-Length: 1000",
        "Host: 7860",
        "Connection: keep-alive",
        "Content-Type: application/json"
    )
    @POST("/sdapi/v1/txt2img")
    fun postTxt2Img(@Body userData: UserInfo) : Call<Txt2ImgResponse>

    @Headers(
        "Host: 7860",
    )
    @GET("/sdapi/v1/sd-models")
    fun getmodels() : Call<List<ModelsResponse>>

    @Headers(
        "Host: 7860",
    )
    @GET("/sdapi/v1/options")
    fun getOptions() : Call<OptionResponse>

    @Headers(
        "Content-Length: 1000",
        "Host: 7860",
        "Connection: keep-alive",
        "Content-Type: application/json"
    )
    @POST("/sdapi/v1/options")
    fun setOptions(@Body option: OptionResponse) : Call<Any>
}

data class Txt2ImgResponse(

    @SerializedName("images")
    var images: ArrayList<String>,

    @SerializedName("parameters")
    var parameters: Any,

    @SerializedName("info")
    var info: Any
)

data class ModelsResponse(

    @SerializedName("title")
    var title: String,
    @SerializedName("model_name")
    var model_name: String,
    @SerializedName("hash")
    var hash: String,
    @SerializedName("sha256")
    var sha256: String,
    @SerializedName("filename")
    var filename: String,
    @SerializedName("config")
    var config: Any,
)

data class OptionResponse(

    @SerializedName("sd_model_checkpoint")
    var model: String,
)

data class UserInfo(
    @SerializedName("prompt") val prompt:String?,
    @SerializedName("steps") val steps:Int?,
    @SerializedName("override_settings") val option:OptionData?,
)
data class OptionData(
    @SerializedName("sd_model_checkpoint") val model:String?,
)

object ServiceBuilder{
    private val txt2imgUrl = "192.168.4.4/sdapi/v1/txt2img"
    private val postApi = "sdapi/v1/txt2img"
    private val client = OkHttpClient.Builder()
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.4.4:7860")
        .addConverterFactory((GsonConverterFactory.create()))
        .client(client)
        .build()

    fun<T> buildServices(service: Class<T>): T{
        return retrofit.create((service))
    }
}

data class ResponseModel(
    val message: String
)