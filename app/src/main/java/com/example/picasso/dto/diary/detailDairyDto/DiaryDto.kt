package com.example.picasso.dto.diary.detailDairyDto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiaryDto (
    @SerializedName("title")
    val title:String,

    @SerializedName("content")
    val content: String,

    @SerializedName("emotion")
    val mood: String,

    @SerializedName("weather")
    val weather: String,

    @SerializedName("source")
    val source: String,

    @SerializedName("diary_id")
    val diary_id: Int,

    @SerializedName("date")
    val date: String,

    @SerializedName("rate")
    val rate: Float?
):Parcelable