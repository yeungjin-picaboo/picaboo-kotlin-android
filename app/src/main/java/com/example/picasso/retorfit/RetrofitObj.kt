package com.example.picasso.retorfit

import com.example.picasso.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObj {
    var api: ApiService
    private const val URL = "https://172.21.1.160:3000" // 예시 : "https://0.0.0.1:8080"

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ApiService::class.java)
    }


}