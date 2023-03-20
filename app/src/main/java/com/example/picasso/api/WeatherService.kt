package com.example.picasso.api

import com.example.picasso.dto.CallDiary
import com.example.picasso.dto.DiaryDto
import com.example.picasso.dto.MoodDto
import com.example.picasso.dto.WeatherDto
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.cdimascio.dotenv.dotenv
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WeatherService {
    @POST("weather-mood")
//    @POST("weather")
    fun getWeatherMood(@Body req: MutableMap<String,String>): Call<WeatherDto>

    fun getUser(@Body req:String): Call<MoodDto>
    @POST("re-calendar")
    suspend fun getDiary(@Body req:CallDiary): Response<DiaryDto>

    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private val dotenv = dotenv {
            directory = "./assets"
            filename = "env"
            ignoreIfMalformed = true
        }
        private val host = dotenv["HOST"]
        private val BASE_URL = host // 주소

        fun communicate(): WeatherService {
            // companion object 즉 전역적으로 사용 가능한 객체에서 WeatherService의 getWeather를 구현할 수 있음.
            // 즉 DiaryActivity의 예에서 330행의 api:WeatherService 에서
            // interface WeatherService가 가지고있는 companion object의 create()를 가져온 다음
            // create는 WeatherService

            val gson: Gson = GsonBuilder().setLenient().create();
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(WeatherService::class.java)
        }
    }
}