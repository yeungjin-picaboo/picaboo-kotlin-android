package com.example.picasso.api

import com.example.picasso.dto.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.cdimascio.dotenv.dotenv
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface WeatherService {
    @POST("weather/ai-weather-mood")
    fun getWeatherMood(@Body req: MutableMap<String, String>): Call<WeatherDto>

    // content로 분석한 날씨, 일기 가져오고 일기 DB에 저장하기
    @POST("diaries/weather-mood")
    fun createDiary(@Body req: JSONObject): Call<Boolean>

    // 일기 생성
    // 타입은 email, content
    // 만약 한번에 할시엔
    // email content mood weather
    fun getUser(@Body req: String): Call<MoodDto>

    @GET("diaries/years/{year}/month/{month}")
    suspend fun getDiary(
        @Path("year") year: String,
        @Path("month") month: String,
        @Body req: CallDiaryDto
    ): Response<DiaryDto>

    // 다이어리 다시 받아오기.
    // 세부 내용에서 다이어리 클릭 시 다이어리 가져오기
    // 보내는 데이터는 eamil , date
    // 받는 데이터는 title, content, mood, weather, diaryId
    @DELETE("diaries/{id}")
    suspend fun deleteDiary(@Path("id") id: Int, userId: String): Response<Boolean>
    // diary의 특정 id 삭제하기

    @PUT("diaries/{id}")
    suspend fun modifyDiary(@Path("id") id: Int, @Body req: JSONObject): Call<Boolean>
    // 수정될 때는 id로 검색
    // jsonObject의 요소는 email, content, diaryId

    @GET("diaries/diaries-list")
    suspend fun getDiariesList(userId: String): Response<DiariesListDto>
    // 매개변수로 email만 주면 됨

    @POST("ex")
    suspend fun registerGoogleUser(googleRegisterDto: GoogleRegisterDto):Response<Boolean>

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