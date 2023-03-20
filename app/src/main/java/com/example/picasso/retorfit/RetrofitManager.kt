package com.example.picasso.retorfit

//import android.os.IInterface
//import com.example.picasso.api.WeatherService
//import io.github.cdimascio.dotenv.dotenv
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//class RetrofitManager(var service:Class<IInterface>) {
//    constructor(service:Class<IInterface>):this(service){
//
//    }
//    companion object{
//        private val dotenv = dotenv {
//            directory = "./assets"
//            filename = "env"
//            ignoreIfMalformed = true
//        }
//        private val host = dotenv["HOST"]
//        private val BASE_URL = host // 주소
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(service::class.java)
//    }
//}