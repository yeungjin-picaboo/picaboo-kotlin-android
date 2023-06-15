package com.example.picasso.api

import android.content.Context
import com.example.picasso.Diary
import com.example.picasso.dto.*
import com.example.picasso.dto.diary.DiariesListDto
import com.example.picasso.dto.diary.detailDairyDto.DiaryDto
import com.example.picasso.dto.diary.detailDairyDto.RatingStarDto
import com.example.picasso.dto.diary.make.WeatherDto
import com.example.picasso.dto.message.ResultMessageDto
import com.example.picasso.dto.sign.ReturnJwtDto
import com.example.picasso.dto.sign.SignInDto
import com.example.picasso.dto.sign.SignUpDto
import com.example.picasso.dto.sign.ValidateLoginDto
import com.example.picasso.dto.userInfo.UserInfoDiaryDto
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.cdimascio.dotenv.dotenv
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    @POST("api/diary/meta")
    fun getWeatherMood(@Body req: MutableMap<String, String>): Call<WeatherDto>
    // 天気と気分を取得し、日記を作成します

    @GET("api/diary/years/{year}/months/{month}")
    suspend fun getAllDiary(
        @Path("year") year: String,
        @Path("month") month: String,
    ): Response<MutableList<Diary>>
    // 指定された年と月のすべての日記を取得します

    @GET("api/diary/{id}")
    suspend fun getDetailedDiary(@Path("id") id: Int): Response<DiaryDto>
    // 指定された日記の詳細情報を取得します

    @DELETE("api/diary/{id}")
    suspend fun deleteDiary(@Path("id") id: Int): Response<ResultMessageDto>
    // 指定された日記を削除します

    @POST("api/diary")
    fun createDiary(@Body req: Any): Call<Unit>
    // 日記を作成します

    @PUT("api/diary/{id}")
    fun modifyDiary(@Path("id") id: Int, @Body req: Any): Call<Unit>
    // 指定された日記を修正します

    @GET("api/diary/dates")
    suspend fun getDiariesList(): Response<List<DiariesListDto>>
    // 日記のリストを取得します

    @POST("api/signup")
    suspend fun signup(@Body signUpDto: SignUpDto): Response<ResultMessageDto>
    // サインアップします

    @POST("api/login")
    suspend fun signIn(@Body loginDto: SignInDto): Response<ReturnJwtDto>
    // ログインします

    @GET("api")
    suspend fun validateLogin(): Response<ValidateLoginDto>
    // ログインの有効性を検証します

    @POST("api/diary/rating")
    suspend fun ratingUser(@Body signUpDto: RatingStarDto): Response<ResultMessageDto>
    // ユーザーの評価を送信します

    @GET("api/users-info")
    suspend fun getUserInfo(): Response<MutableList<UserInfoDiaryDto>>
    // ユーザーの情報を取得します

    companion object {
        private val dotenv = dotenv {
            directory = "./assets"
            filename = "env"
            ignoreIfMalformed = true
        }
        private val host = dotenv["HOST"]
        private val BASE_URL = host // アドレス

        private val gson: Gson = GsonBuilder().setLenient().create()

        fun communicate(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ApiService::class.java)
        }

        fun communicateJwt(context: Context): ApiService {
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
                .create(ApiService::class.java)
        }

        private fun getAccessToken(context: Context): String {
            val sharedPreferences =
                context.getSharedPreferences("JWT", Context.MODE_PRIVATE)
            return sharedPreferences.getString("JWT", "") ?: ""
        }
    }
}
