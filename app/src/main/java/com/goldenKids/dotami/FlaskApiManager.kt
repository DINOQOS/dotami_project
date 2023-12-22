package com.goldenKids.dotami


import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class UploadImageRequest(
    val date: String,
    val location: String,
    val uid: String,
    val image_data: String
)

interface FlaskApiService {
    @POST("/upload")
    fun uploadImage(@Body request: UploadImageRequest): Call<UploadImageResponse>
}
data class UploadImageResponse(val message: String, val image_url: String,val error: String)

class FlaskApiManager {
    private val BASE_URL = "http://43.201.22.205/"

    private val retrofit: Retrofit

    init {
        val httpClient = OkHttpClient.Builder().build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }
    fun uploadImage(
        date: String,
        location: String,
        uid: String,
        base64ImageData: String,
        callback: (response: UploadImageResponse?) -> Unit
    ) {
        val apiService = retrofit.create(FlaskApiService::class.java)
        val request = UploadImageRequest(date, location, uid, base64ImageData)
        val call = apiService.uploadImage(request)

        call.enqueue(object : retrofit2.Callback<UploadImageResponse> {
            override fun onResponse(call: Call<UploadImageResponse>, response: retrofit2.Response<UploadImageResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    val errorMessage = "서버 응답 실패 - HTTP 상태 코드: ${response.code()}"
                    callback(null)
                }
            }

            override fun onFailure(call: Call<UploadImageResponse>, t: Throwable) {
                println("서버 통신 실패: ${t.message}")
                callback(null)
            }
        })
    }

}