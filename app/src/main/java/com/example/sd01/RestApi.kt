package com.example.sd01


import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName
import okhttp3.ConnectionPool
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import org.apache.http.conn.ssl.SSLSocketFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import java.io.FileInputStream
import java.io.InputStream
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.collections.ArrayList

const val host = 443
interface RestApi {
    @Headers(
        "Content-Length: 1000",
        "Host: $host",
        "Connection: keep-alive",
        "Content-Type: application/json"
    )
    @POST("/sdapi/v1/txt2img")
    fun postTxt2Img(@Body userData: UserInfo) : Call<Txt2ImgResponse>

    @Headers(
        "Host: $host",
    )
    @GET("/sdapi/v1/sd-models")
    fun getmodels() : Call<List<ModelsResponse>>

    @Headers(
        "Host: $host",
    )
    @GET("/sdapi/v1/options")
    fun getOptions() : Call<OptionResponse>

    @Headers(
        "Host: $host",
    )
    @GET("/sdapi/v1/getLora")
    fun getLoras() : Call<LoraModelResponse>

    @Headers(
        "Content-Length: 1000",
        "Host: $host",
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

data class LoraModelResponse(

    @SerializedName("list")
    var loras: ArrayList<String>,
)

data class UserInfo(
    @SerializedName("prompt") val prompt:String? = "highres,8k,masterpiece, best quality,instagram most viewed,Megapixel,illustration",
    @SerializedName("negative_prompt") val negativePrompt:String? = "lowers, bad anatomy, bad hands,text, error, missing fingers,extra digit,fewer digits, cropped, worst quality, low quality, normal quality, jpeg artifacts, signature, watermark,username, blurry",
    @SerializedName("steps") val steps:Int? = 50,
    @SerializedName("override_settings") val option:OptionData?,
    @SerializedName("script_args") val args:JsonArray,
    @SerializedName("seed") val seed:Int = -1,
    @SerializedName("sampler_index") val sampler:String = "Euler a",
    @SerializedName("width") val width:Int= 512,
    @SerializedName("height") val height:Int = 512,
    @SerializedName("restore_faces") val restoreFace:Boolean = true,
    @SerializedName("script_name") val script_name:String = "additional_networks",
)
data class OptionData(
    @SerializedName("sd_model_checkpoint") val model:String?,
)

data class ArgsData(
    @SerializedName("AddNet Enabled") val addNetEnabled:Boolean?,
    @SerializedName("AddNet Separate Weights") val addNetSeparateWeights:Boolean?,
    @SerializedName("AddNet Module 1") val addNetModule1:String?,
    @SerializedName("AddNet Model 1") val addNetModel1:String?,
    @SerializedName("AddNet Weight A 1") val addNetWeightA1:Int?,
    @SerializedName("AddNet Weight B 1") val addNetWeightB1:Int?,
    @SerializedName("AddNet Module 2") val addNetModule2:String?,
    @SerializedName("AddNet Model 2") val addNetModel2:String?,
    @SerializedName("AddNet Weight A 2") val addNetWeightA2:Int?,
    @SerializedName("AddNet Weight B 2") val addNetWeightB2:Int?,
)

object ServiceBuilder{
    private val txt2imgUrl = "https://192.168.4.4:7860"
//    private val txt2imgUrl = "https://2939086s6o.yicp.fun:443"
    private val postApi = "sdapi/v1/txt2img"
    private val client = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS)
        .callTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .writeTimeout(100, TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(10, 100, TimeUnit.SECONDS))
        .retryOnConnectionFailure(true)
//        .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(txt2imgUrl)
        .addConverterFactory((GsonConverterFactory.create()))
        .client(client)
        .build()

    fun<T> buildServices(service: Class<T>): T{
        return retrofit.create((service))
    }
}

//fun getUnsafeOkHttpClient(): OkHttpClient? {
//    return try {
//        // Create a trust manager that does not validate certificate chains
//        val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(object : X509TrustManager {
//            @Throws(CertificateException::class)
//            override fun checkClientTrusted(
//                chain: Array<X509Certificate?>?,
//                authType: String?
//            ) {
//            }
//
//            @Throws(CertificateException::class)
//            override fun checkServerTrusted(
//                chain: Array<X509Certificate?>?,
//                authType: String?
//            ) {
//            }
//
//            override fun getAcceptedIssuers(): Array<X509Certificate> {
//                val inStream: InputStream = FileInputStream("fileName-of-cert")
//                val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
//                val cert = cf.generateCertificate(inStream) as X509Certificate
//                inStream.close()
//                var res = Array(1){cert}
//                return res
//            }
//
//
//            val acceptedIssuers: Array<X509Certificate?>?
//                get() = arrayOfNulls(0)
//        })
//
//        // Install the all-trusting trust manager
//        val sslContext: SSLContext = SSLContext.getInstance("TLS")
//        sslContext.init(
//            null, trustAllCerts,
//            SecureRandom()
//        )
//        // Create an ssl socket factory with our all-trusting manager
//        val sslSocketFactory: javax.net.ssl.SSLSocketFactory? = sslContext
//            .getSocketFactory()
//        var okHttpClient = OkHttpClient()
//        okHttpClient = okHttpClient.newBuilder()
//            .sslSocketFactory(sslSocketFactory)
//            .hostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build()
//        okHttpClient
//    } catch (e: Exception) {
//        throw RuntimeException(e)
//    }
//}

data class ResponseModel(
    val message: String
)