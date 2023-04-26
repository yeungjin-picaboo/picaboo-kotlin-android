package com.example.picasso.api

import android.content.Context
import android.database.Observable
import com.example.picasso.Diary
import com.example.picasso.dto.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.cdimascio.dotenv.dotenv
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface WeatherService {
    @POST("api/diary/meta")
    fun getWeatherMood(@Body req: MutableMap<String, String>): Call<WeatherDto>
    // content로 분석한 날씨, 일기 가져오고 일기 DB에 저장하기


    // 일기 생성
    // 타입은 email, content
    // 만약 한번에 할시엔
    // email content mood weather
//    fun getUser(@Body req: String): Call<MoodDto>

    @GET("api/diary/years/{year}/months/{month}")
    suspend fun getAllDiary(
        @Path("year") year: String,
        @Path("month") month: String,
    ): Response<MutableList<Diary>>

    @GET("api/diary/{id}")
    suspend fun getDetailedDiary(@Path("id") id: Int): Response<DiaryDto>

    @GET("diary/years/date")
    suspend fun getDiary(
        @Body date: String
    ): Response<DiaryDto>

    // 다이어리 다시 받아오기.
    // 세부 내용에서 다이어리 클릭 시 다이어리 가져오기
    // 보내는 데이터는 eamil , date
    // 받는 데이터는 title, content, mood, weather, diaryId
    @DELETE("api/diary/{id}")
    suspend fun deleteDiary(@Path("id") id: Int): Response<ResultMessageDto>
    // diary의 특정 id 삭제하기

    @POST("api/diary")
    fun createDiary(@Body req: Any): Call<Unit>

    @PUT("api/diary/{id}")
    fun modifyDiary(@Path("id") id: Int, @Body req: Any): Call<Unit>
    // 수정될 때는 id로 검색


    @GET("api/diary/dates")
    suspend fun getDiariesList(): Response<List<DiariesListDto>>
    // 매개변수로 email만 주면 됨

    @POST("api/signup")
    suspend fun signup(@Body signUpDto: SignUpDto): Response<ResultMessageDto>

    @POST("api/login")
    suspend fun signIn(@Body loginDto: SignInDto): Response<ReturnJwtDto>

    @GET("api")
    suspend fun validateLogin(): Response<ValidateLogin>

    companion object {
        private val dotenv = dotenv {
            directory = "./assets"
            filename = "env"
            ignoreIfMalformed = true
        }
        private val host = dotenv["HOST"]
        private val BASE_URL = host // address

        private val gson: Gson = GsonBuilder().setLenient().create()

        fun communicate(): WeatherService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(WeatherService::class.java)
        }

        fun communicateJwt(context: Context): WeatherService {
            val accessToken = getAccessToken(context)

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $accessToken")
                        .build()
                    chain.proceed(request)
                }
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(WeatherService::class.java)
        }

        private fun getAccessToken(context: Context): String {
            val sharedPreferences =
                context.getSharedPreferences("JWT", Context.MODE_PRIVATE)
            return sharedPreferences.getString("JWT", "") ?: ""
        }
    }
}
