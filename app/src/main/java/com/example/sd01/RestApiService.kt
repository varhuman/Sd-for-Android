package com.example.sd01

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestApiService {
    fun postTxt2Img(userData: UserInfo, onResult: (Txt2ImgResponse?) -> Unit){
        val retrofit = ServiceBuilder.buildServices(RestApi::class.java)
        retrofit.postTxt2Img(userData).enqueue(
            object : Callback<Txt2ImgResponse> {
                override fun onResponse(call: Call<Txt2ImgResponse>, response: Response<Txt2ImgResponse>) {
                    val w = response.body()
                    onResult(w)
                }

                override fun onFailure(call: Call<Txt2ImgResponse>, t: Throwable) {
                    onResult(null)
                }
            }
        )
    }

    fun getModels(onResult: (List<ModelsResponse>?) -> Unit){
        val retrofit = ServiceBuilder.buildServices(RestApi::class.java)
        retrofit.getmodels().enqueue(
            object : Callback<List<ModelsResponse>> {
                override fun onResponse(call: Call<List<ModelsResponse>>, response: Response<List<ModelsResponse>>) {
                    val w = response.body()
                    onResult(w)
                }

                override fun onFailure(call: Call<List<ModelsResponse>>, t: Throwable) {
                    onResult(null)
                }
            }
        )
    }

    fun getOptions(onResult: (OptionResponse?) -> Unit){
        val retrofit = ServiceBuilder.buildServices(RestApi::class.java)
        retrofit.getOptions().enqueue(
            object : Callback<OptionResponse> {
                override fun onResponse(call: Call<OptionResponse>, response: Response<OptionResponse>) {
                    val w = response.body()
                    onResult(w)
                }

                override fun onFailure(call: Call<OptionResponse>, t: Throwable) {
                    onResult(null)
                }
            }
        )
    }

    fun setOptions(option: OptionResponse, onResult: (Any?) -> Unit){
        val retrofit = ServiceBuilder.buildServices(RestApi::class.java)
        retrofit.setOptions(option).enqueue(
            object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    val w = response.body()
                    onResult(w)
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                }
            }
        )
    }
}