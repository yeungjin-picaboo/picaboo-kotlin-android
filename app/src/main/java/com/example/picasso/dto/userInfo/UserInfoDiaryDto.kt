package com.example.picasso.dto.userInfo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfoDiaryDto(
    @SerializedName("diary_title")
    val title: String,

    @SerializedName("diary_content")
    val content: String,

    @SerializedName("diary_emotion")
    val emotion: String,

    @SerializedName("diary_weather")
    val weather: String,

    @SerializedName("diary_source")
    val source: String,

    @SerializedName("diary_diary_id")
    val diary_id: Int,

    @SerializedName("diary_date")
    val date: String,

    @SerializedName("diary_rate")
    val rate: Float?
) : Parcelable

