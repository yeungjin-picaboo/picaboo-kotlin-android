package com.example.picasso.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class WeatherDto(
    @SerializedName("weather") // 받아온 데이터와 일치시키면 변수명 바꿔 사용 가능
    val weather: String,

    @SerializedName("mood")
    val mood: String
):Parcelable